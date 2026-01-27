package com.bb.weather.weather_service.config;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.mysqlclient.MySQLPool;

public class DbConfig {

  public static MySQLPool getClient(Vertx vertx) {

    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(3306)
      .setHost("127.0.0.1")
      .setDatabase("weather_db")
      .setUser("weather_user")
      .setPassword("weather040604")
      .setSsl(false);

    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

    return MySQLPool.pool(vertx, connectOptions, poolOptions);
  }
}
