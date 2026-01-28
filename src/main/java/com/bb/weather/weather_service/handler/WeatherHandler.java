package com.bb.weather.weather_service.handler;

import com.bb.weather.weather_service.service.WeatherService;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import javax.inject.Inject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * WeatherHandler
 * Handles HTTP requests and delegates to WeatherService.
 * FULL logic preserved from WeatherRouter.
 */
public class WeatherHandler {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(WeatherHandler.class);

  private final WeatherService weatherService;

  @Inject
  public WeatherHandler(WeatherService weatherService) {
    this.weatherService = weatherService;
  }

  public void getWeatherByCity(RoutingContext ctx) {

    String city = ctx.request().getParam("city");

    if (city == null || city.isEmpty()) {
      ctx.response()
        .setStatusCode(400)
        .end("Query parameter 'city' is required");
      return;
    }

    Promise<JsonObject> promise = Promise.promise();

    weatherService.getWeatherByCity(city, promise);

    promise.future()
      .onSuccess(res -> ctx.response()
        .putHeader("Content-Type", "application/json")
        .end(res.encode()))
      .onFailure(err -> {
        LOGGER.error("Failed to fetch weather by city", err);
        ctx.response()
          .setStatusCode(500)
          .end(err.getMessage());
      });
  }

  public void getWeatherByLatLon(RoutingContext ctx) {

    String latStr = ctx.request().getParam("lat");
    String lonStr = ctx.request().getParam("lon");

    if (latStr == null || lonStr == null) {
      ctx.response()
        .setStatusCode(400)
        .end("Query params 'lat' and 'lon' are required");
      return;
    }

    double lat;
    double lon;

    try {
      lat = Double.parseDouble(latStr);
      lon = Double.parseDouble(lonStr);
    } catch (NumberFormatException e) {
      ctx.response()
        .setStatusCode(400)
        .end("Invalid latitude or longitude");
      return;
    }

    Promise<JsonObject> promise = Promise.promise();
    weatherService.getWeatherByLatLon(lat, lon, promise);

    promise.future()
      .onSuccess(res -> ctx.response()
        .putHeader("Content-Type", "application/json")
        .end(res.encode()))
      .onFailure(err -> {
        LOGGER.error("Failed to fetch weather by lat/lon", err);
        ctx.response()
          .setStatusCode(500)
          .end(err.getMessage());
      });
  }

  public void searchWeatherByCity(RoutingContext ctx) {

    String city = ctx.request().getParam("city");

    if (city == null || city.isEmpty()) {
      ctx.response()
        .setStatusCode(400)
        .end("Query parameter 'city' is required");
      return;
    }

    Promise<JsonObject> promise = Promise.promise();
    weatherService.searchWeatherByCity(city, promise);

    promise.future()
      .onSuccess(result -> ctx.response()
        .putHeader("Content-Type", "application/json")
        .end(result.encode()))
      .onFailure(err -> {
        LOGGER.error("Search failed", err);
        ctx.response()
          .setStatusCode(404)
          .end(err.getMessage());
      });
  }

  public void updateWeather(RoutingContext ctx) {

    JsonObject body = ctx.getBodyAsJson();

    if (body == null || body.getString("city") == null) {
      ctx.response()
        .setStatusCode(400)
        .end("Request body with 'city' is required");
      return;
    }

    Promise<Void> promise = Promise.promise();
    weatherService.updateWeather(body, promise);

    promise.future()
      .onSuccess(v -> ctx.response()
        .putHeader("Content-Type", "application/json")
        .end(
          new JsonObject()
            .put("message", "Weather updated Successfully")
            .encode()
        ))
      .onFailure(err -> {
        LOGGER.error("Update failed", err);
        ctx.response()
          .setStatusCode(500)
          .end(err.getMessage());
      });
  }
}
