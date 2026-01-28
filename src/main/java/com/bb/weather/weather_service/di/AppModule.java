package com.bb.weather.weather_service.di;

import dagger.Module;
import dagger.Provides;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.mysqlclient.MySQLPool;

import com.bb.weather.weather_service.config.DbConfig;

@Module
public class AppModule {

  private final Vertx vertx;

  // We pass Vertx from MainVerticle
  public AppModule(Vertx vertx) {
    this.vertx = vertx;
  }

  @Provides
  Vertx provideVertx() {
    return vertx;
  }

  @Provides
  WebClient provideWebClient(Vertx vertx) {
    return WebClient.create(vertx);
  }

  @Provides
  MySQLPool provideMySQLPool(Vertx vertx) {
    return DbConfig.getClient(vertx);
  }
}
