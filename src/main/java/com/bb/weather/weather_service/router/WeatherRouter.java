package com.bb.weather.weather_service.router;

import com.bb.weather.weather_service.service.WeatherService;
import io.vertx.core.Vertx;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonObject;

public class WeatherRouter {

  public static void register(Router router, Vertx vertx) {

    WeatherService weatherService = new WeatherService(vertx);

    router.get("/api/weather").handler(ctx -> {
      String city = ctx.request().getParam("city");
      Promise<JsonObject> promise = Promise.promise();

      weatherService.getWeatherByCity(city, promise);

      promise.future()
        .onSuccess(res -> ctx.response()
          .putHeader("Content-Type", "application/json")
          .end(res.encode()))
        .onFailure(err -> ctx.response()
          .setStatusCode(500)
          .end(err.getMessage()));
    });

    router.get("/api/weather/geo").handler(ctx -> {

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
        .onFailure(err -> ctx.response()
          .setStatusCode(500)
          .end(err.getMessage()));
    });


    router.get("/api/weather/search").handler(ctx -> {

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
        .onFailure(err -> ctx.response()
          .setStatusCode(404)
          .end(err.getMessage()));
    });

    router.put("/api/weather").handler (ctx -> {

      JsonObject body = ctx.getBodyAsJson();

      if(body == null || body.getString("city") == null) {
        ctx.response ()
          .setStatusCode(400)
          .end("Request body with 'city' is requird");
        return;
      }

      Promise<Void> promise = Promise.promise();
      weatherService.updateWeather(body, promise);

      promise.future()
        .onSuccess(v -> ctx.response()
          .putHeader("Content-Type", "application/json")
          .end(
            new JsonObject().put("message","Weather updated Successfully")
              .encode()
          )
        )
        .onFailure(err -> ctx.response()
            .setStatusCode(500)
            .end(err.getMessage())
        );
    });
  }
}
