package com.bb.weather.weather_service.service;

import io.vertx.core.Vertx;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import com.bb.weather.weather_service.dao.WeatherDaoImpl;


import com.bb.weather.weather_service.config.DbConfig;
import com.bb.weather.weather_service.dao.WeatherDao;

import io.vertx.mysqlclient.MySQLPool;

public class WeatherServiceImpl implements WeatherService {

  private final WebClient webClient;
  private final WeatherDao weatherDao;

  private static final String API_KEY = "61cf1dd1249a8b953c0957bdd359f6bc";

  public WeatherServiceImpl(Vertx vertx) {
    this.webClient = WebClient.create(vertx);
    MySQLPool client = DbConfig.getClient(vertx);
    this.weatherDao = new WeatherDaoImpl(client);
  }

  @Override
  public void getWeatherByCity(String city, Promise<JsonObject> promise) {

    String uri = "/data/2.5/weather?q=" + city +
      "&appid=" + API_KEY +
      "&units=metric";

    webClient
      .get(443, "api.openweathermap.org", uri)
      .ssl(true)
      .send(ar -> {
        if (ar.failed()) {
          promise.fail(ar.cause());
          return;
        }

        JsonObject body = ar.result().bodyAsJsonObject();

        JsonObject response = new JsonObject()
          .put("city", body.getString("name"))
          .put("latitude", body.getJsonObject("coord").getDouble("lat"))
          .put("longitude", body.getJsonObject("coord").getDouble("lon"))
          .put("temperature", body.getJsonObject("main").getDouble("temp"))
          .put("humidity", body.getJsonObject("main").getInteger("humidity"))
          .put("pressure", body.getJsonObject("main").getInteger("pressure"))
          .put("weather_main",
            body.getJsonArray("weather").getJsonObject(0).getString("main"))
          .put("weather_description",
            body.getJsonArray("weather").getJsonObject(0).getString("description"))
          .put("wind_speed",
            body.getJsonObject("wind").getDouble("speed"))
          .put("visibility",
            body.getInteger("visibility"));

        Promise<Boolean> existsPromise = Promise.promise();
        weatherDao.existsByCity(city, existsPromise);

        existsPromise.future()
          .onSuccess(exists -> {

            Promise<Void> dbPromise = Promise.promise();

            if (exists) {
              weatherDao.updateWeather(response, dbPromise);
            } else {
              weatherDao.saveWeather(response, dbPromise);
            }

            dbPromise.future()
              .onSuccess(v -> promise.complete(response))
              .onFailure(promise::fail);

          })
          .onFailure(promise::fail);
      });
  }

  @Override
  public void getWeatherByLatLon(double lat, double lon, Promise<JsonObject> promise) {

    String uri = "/data/2.5/weather?lat=" + lat +
      "&lon=" + lon +
      "&appid=" + API_KEY +
      "&units=metric";

    webClient
      .get(443, "api.openweathermap.org", uri)
      .ssl(true)
      .send(ar -> {
        if (ar.failed()) {
          promise.fail(ar.cause());
          return;
        }

        JsonObject body = ar.result().bodyAsJsonObject();

        JsonObject response = new JsonObject()
          .put("city", body.getString("name"))
          .put("latitude", body.getJsonObject("coord").getDouble("lat"))
          .put("longitude", body.getJsonObject("coord").getDouble("lon"))
          .put("temperature", body.getJsonObject("main").getDouble("temp"))
          .put("humidity", body.getJsonObject("main").getInteger("humidity"))
          .put("pressure", body.getJsonObject("main").getInteger("pressure"))
          .put("weather_main",
            body.getJsonArray("weather").getJsonObject(0).getString("main"))
          .put("weather_description",
            body.getJsonArray("weather").getJsonObject(0).getString("description"))
          .put("wind_speed",
            body.getJsonObject("wind").getDouble("speed"))
          .put("visibility",
            body.getInteger("visibility"));

        String city = response.getString("city");

        Promise<Boolean> existsPromise = Promise.promise();
        weatherDao.existsByCity(city, existsPromise);

        existsPromise.future()
          .onSuccess(exists -> {

            Promise<Void> dbPromise = Promise.promise();

            if (exists) {
              weatherDao.updateWeather(response, dbPromise);
            } else {
              weatherDao.saveWeather(response, dbPromise);
            }

            dbPromise.future()
              .onSuccess(v -> promise.complete(response))
              .onFailure(promise::fail);

          })
          .onFailure(promise::fail);
      });
  }

  @Override
  public void searchWeatherByCity(String city, Promise<JsonObject> promise) {
    weatherDao.searchByCity(city, promise);
  }

  @Override
  public void updateWeather(JsonObject weather, Promise<Void> promise) {
    weatherDao.updateWeather(weather, promise);
  }
}
