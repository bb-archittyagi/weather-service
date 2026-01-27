package com.bb.weather.weather_service;

import com.bb.weather.weather_service.router.HealthRouter;
import com.bb.weather.weather_service.router.WeatherRouter;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {

    Router router = Router.router(vertx);
    router.route().handler(StaticHandler.create());

    router.route().handler(BodyHandler.create());
    HealthRouter.register(router, vertx);
    WeatherRouter.register(router, vertx);


    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080, http -> {
        if (http.succeeded()) {
          System.out.println("HTTP server started on port 8080");
          startPromise.complete();
        } else {
          startPromise.fail(http.cause());
        }
      });
  }
}
