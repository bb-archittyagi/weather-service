package com.bb.weather.weather_service.controller.http;

import io.vertx.ext.web.Router;

import javax.inject.Inject;

public class HealthController {

  @Inject
  public HealthController() {
    // Dagger DI constructor
  }

  public void registerRoutes(Router router) {

    // Health check endpoint
    // GET /api/health
    router.get("/health")
      .handler(ctx -> ctx.response().end("OK"));
  }
}
