package com.bb.weather.weather_service.service;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public interface WeatherService {

  void getWeatherByCity(String city, Promise<JsonObject> promise);

  void getWeatherByLatLon(double lat, double lon, Promise<JsonObject> promise);

  void searchWeatherByCity(String city, Promise<JsonObject> promise);

  void updateWeather(JsonObject weather, Promise<Void> promise);
}
