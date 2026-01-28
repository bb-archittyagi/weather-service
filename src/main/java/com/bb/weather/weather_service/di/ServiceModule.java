package com.bb.weather.weather_service.di;

import dagger.Binds;
import dagger.Module;

import com.bb.weather.weather_service.dao.WeatherDao;
import com.bb.weather.weather_service.dao.WeatherDaoImpl;
import com.bb.weather.weather_service.service.WeatherService;
import com.bb.weather.weather_service.service.WeatherServiceImpl;

@Module
public abstract class ServiceModule {

  @Binds
  abstract WeatherDao bindWeatherDao(WeatherDaoImpl impl);

  @Binds
  abstract WeatherService bindWeatherService(WeatherServiceImpl impl);
}
