package com.bb.weather.weather_service.controller.http;

import com.bb.weather.weather_service.handler.WeatherHandler;
import io.vertx.ext.web.Router;
import javax.inject.Inject;

/**
 * WeatherController
 * Responsible only for URL mappings.
 */
public class WeatherController {

  private final WeatherHandler weatherHandler;

  @Inject
  public WeatherController(WeatherHandler weatherHandler) {
    this.weatherHandler = weatherHandler;
  }
  public void register(Router router) {

    router.get("/api/weather")
      .handler(weatherHandler::getWeatherByCity);

    router.get("/api/weather/geo")
      .handler(weatherHandler::getWeatherByLatLon);

    router.get("/api/weather/search")
      .handler(weatherHandler::searchWeatherByCity);

    router.put("/api/weather")
      .handler(weatherHandler::updateWeather);
  }
}
