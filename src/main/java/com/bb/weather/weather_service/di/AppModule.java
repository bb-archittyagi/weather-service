package com.bb.weather.weather_service.di;

import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;

@Module
public class AppModule {

  private final Vertx vertx;

  public AppModule(Vertx vertx) {
    this.vertx = vertx;
  }

  @Provides
  Vertx provideVertx() {
    return vertx;
  }
}
