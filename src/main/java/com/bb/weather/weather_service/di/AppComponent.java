package com.bb.weather.weather_service.di;

import dagger.Component;

import com.bb.weather.weather_service.controller.http.WeatherController;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
  AppModule.class,
  ServiceModule.class
})
public interface AppComponent {

  WeatherController weatherController();
}
