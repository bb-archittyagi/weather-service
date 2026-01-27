package com.bb.weather.weather_service.dao;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Tuple;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * WeatherDao implementation.
 * FULL logic copied from original WeatherDao.java
 * No behavior change.
 */
public class WeatherDaoImpl implements WeatherDao {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(WeatherDaoImpl.class);

  private final MySQLPool client;

  public WeatherDaoImpl(MySQLPool client) {
    this.client = client;
  }

  @Override
  public void saveWeather(JsonObject weather, Promise<Void> promise) {

    String sql =
      "INSERT INTO weather_data (" +
        "city, latitude, longitude, temperature, humidity, pressure, " +
        "weather_main, weather_description, wind_speed, visibility" +
        ") VALUES (" +
        "'" + weather.getString("city").replace("'", "''") + "', " +
        weather.getDouble("latitude") + ", " +
        weather.getDouble("longitude") + ", " +
        weather.getDouble("temperature") + ", " +
        weather.getInteger("humidity") + ", " +
        weather.getInteger("pressure") + ", " +
        "'" + weather.getString("weather_main").replace("'", "''") + "', " +
        "'" + weather.getString("weather_description").replace("'", "''") + "', " +
        weather.getDouble("wind_speed") + ", " +
        weather.getInteger("visibility") +
        ")";

    client.getConnection(ar -> {
      if (ar.failed()) {
        LOGGER.error("Failed to get DB connection", ar.cause());
        promise.fail(ar.cause());
        return;
      }

      ar.result().query(sql).execute(res -> {
        ar.result().close();

        if (res.succeeded()) {
          LOGGER.info("Weather data inserted for city {}", weather.getString("city"));
          promise.complete();
        } else {
          LOGGER.error("Insert failed", res.cause());
          promise.fail(res.cause());
        }
      });
    });
  }

  @Override
  public void searchByCity(String city, Promise<JsonObject> promise) {

    String sql =
      "SELECT city, latitude, longitude, temperature, humidity, pressure, " +
        "weather_main, weather_description, wind_speed, visibility, created_at " +
        "FROM weather_data " +
        "WHERE city = '" + city.replace("'", "''") + "' " +
        "ORDER BY created_at DESC " +
        "LIMIT 1";

    client.getConnection(ar -> {
      if (ar.failed()) {
        LOGGER.error("Failed to get DB connection", ar.cause());
        promise.fail(ar.cause());
        return;
      }

      var conn = ar.result();

      conn.query(sql).execute(res -> {
        conn.close();

        if (res.failed()) {
          LOGGER.error("Search query failed", res.cause());
          promise.fail(res.cause());
          return;
        }

        if (!res.result().iterator().hasNext()) {
          promise.fail("No data found for city: " + city);
          return;
        }

        var row = res.result().iterator().next();

        JsonObject result = new JsonObject()
          .put("city", row.getString("city"))
          .put("latitude", row.getDouble("latitude"))
          .put("longitude", row.getDouble("longitude"))
          .put("temperature", row.getDouble("temperature"))
          .put("humidity", row.getInteger("humidity"))
          .put("pressure", row.getInteger("pressure"))
          .put("weather_main", row.getString("weather_main"))
          .put("weather_description", row.getString("weather_description"))
          .put("wind_speed", row.getDouble("wind_speed"))
          .put("visibility", row.getInteger("visibility"))
          .put("created_at", row.getLocalDateTime("created_at").toString());

        promise.complete(result);
      });
    });
  }

  @Override
  public void updateWeather(JsonObject weather, Promise<Void> promise) {

    String sql =
      "UPDATE weather_data SET " +
        "latitude = " + weather.getDouble("latitude") + ", " +
        "longitude = " + weather.getDouble("longitude") + ", " +
        "temperature = " + weather.getDouble("temperature") + ", " +
        "humidity = " + weather.getInteger("humidity") + ", " +
        "pressure = " + weather.getInteger("pressure") + ", " +
        "weather_main = '" + weather.getString("weather_main").replace("'", "''") + "', " +
        "weather_description = '" + weather.getString("weather_description").replace("'", "''") + "', " +
        "wind_speed = " + weather.getDouble("wind_speed") + ", " +
        "visibility = " + weather.getInteger("visibility") + " " +
        "WHERE city = '" + weather.getString("city").replace("'", "''") + "'";

    client.getConnection(ar -> {
      if (ar.failed()) {
        LOGGER.error("Failed to get DB connection", ar.cause());
        promise.fail(ar.cause());
        return;
      }

      var conn = ar.result();

      conn.query(sql).execute(res -> {
        conn.close();

        if (res.failed()) {
          LOGGER.error("Update failed", res.cause());
          promise.fail(res.cause());
          return;
        }

        if (res.result().rowCount() == 0) {
          promise.fail("No record found for city: " + weather.getString("city"));
          return;
        }

        LOGGER.info("Weather updated for city {}", weather.getString("city"));
        promise.complete();
      });
    });
  }

  @Override
  public void existsByCity(String city, Promise<Boolean> promise) {

    String sql =
      "SELECT 1 FROM weather_data WHERE city = '" +
        city.replace("'", "''") + "' LIMIT 1";

    client.getConnection(ar -> {
      if (ar.failed()) {
        LOGGER.error("Failed to get DB connection", ar.cause());
        promise.fail(ar.cause());
        return;
      }

      var conn = ar.result();

      conn.query(sql).execute(res -> {
        conn.close();

        if (res.failed()) {
          promise.fail(res.cause());
          return;
        }

        promise.complete(res.result().size() > 0);
      });
    });
  }
}
