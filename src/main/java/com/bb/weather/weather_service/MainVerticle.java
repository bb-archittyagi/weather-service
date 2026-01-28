package com.bb.weather.weather_service;

import com.bb.weather.weather_service.config.DbConfig;
import com.bb.weather.weather_service.controller.http.WeatherController;
import com.bb.weather.weather_service.di.AppComponent;
import com.bb.weather.weather_service.di.AppModule;
import com.bb.weather.weather_service.di.DaggerAppComponent;
import com.bb.weather.weather_service.controller.http.WeatherController;


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


    AppComponent component =
      DaggerAppComponent.builder()
        .appModule(new AppModule(vertx))
        .build();

    WeatherController weatherController =
      component.weatherController();

    weatherController.register(router);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8070, ar -> {
        if (ar.succeeded()) {
          startPromise.complete();
        } else {
          startPromise.fail(ar.cause());
        }
      });

  }
}
