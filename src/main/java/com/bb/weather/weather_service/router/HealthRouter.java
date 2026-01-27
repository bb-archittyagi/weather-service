package com.bb.weather.weather_service.router;

import io.vertx.ext.web.Router;
import io.vertx.core.Vertx;

public class HealthRouter {

  public static void register(Router router, Vertx vertx) {

    router.get("/health").handler(ctx -> {
      ctx.response()
        .putHeader("content-type", "application/json")
        .end("{\"status\":\"UP\"}");
    });
  }
}
