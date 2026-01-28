package com.bb.weather.weather_service.dao;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

/**
 * DAO interface for Weather operations.
 * Logic remains SAME as original WeatherDao,
 * only responsibilities are formalized via interface.
 */
public interface WeatherDao {

  void saveWeather(JsonObject weather, Promise<Void> promise);

  void updateWeather(JsonObject weather, Promise<Void> promise);

  void searchByCity(String city, Promise<JsonObject> promise);

  void existsByCity(String city, Promise<Boolean> promise);
}
