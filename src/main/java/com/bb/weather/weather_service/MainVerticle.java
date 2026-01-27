package com.bb.weather.weather_service;

import com.bb.weather.weather_service.config.DbConfig;
import com.bb.weather.weather_service.controller.http.WeatherController;
import com.bb.weather.weather_service.dao.WeatherDao;
import com.bb.weather.weather_service.dao.WeatherDaoImpl;
import com.bb.weather.weather_service.handler.WeatherHandler;
import com.bb.weather.weather_service.service.WeatherService;
import com.bb.weather.weather_service.service.WeatherServiceImpl;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {

    // Create router
    Router router = Router.router(vertx);

    // Static + body handlers (same behavior as before)
    router.route("/*").handler(StaticHandler.create());
// Body handler ONLY for API routes
    router.route("/api/*").handler(BodyHandler.create());


    // ---- MANUAL DI (Dagger-ready) ----
    WeatherDao weatherDao =
      new WeatherDaoImpl(DbConfig.getClient(vertx));

    WeatherService weatherService =
      new WeatherServiceImpl(vertx);

    WeatherHandler weatherHandler =
      new WeatherHandler(weatherService);

    WeatherController weatherController =
      new WeatherController(weatherHandler);

    // Register controllers
    weatherController.register(router);
    // Start server
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8070, http -> {
        if (http.succeeded()) {
          LOGGER.info("HTTP server started on port 8070");
          startPromise.complete();
        } else {
          LOGGER.error("Failed to start HTTP server", http.cause());
          startPromise.fail(http.cause());
        }
      });
  }
}
