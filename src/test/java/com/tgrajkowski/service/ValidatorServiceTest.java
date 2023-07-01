package com.tgrajkowski.service;

import com.tgrajkowski.com.mycompany.app.model.Forecast;
import com.tgrajkowski.com.mycompany.app.model.ForecastDaily;
import com.tgrajkowski.exception.ExceptionEnum;
import com.tgrajkowski.exception.WeatherException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorServiceTest {
    private ValidatorService validatorService;

    @BeforeEach
    public void setup() {
        validatorService = new ValidatorService();
    }


    @Test
    public void validateForecast_WhenDailyForecastIsNull_ShouldThrowException() {
        Forecast forecast = new Forecast();

        assertThrows(WeatherException.class, () -> validatorService.validateForecast(forecast), ExceptionEnum.FORECAST_DAILY_NULL.getMessage());
    }

    @Test
    public void validateForecast_WhenSunriseIsNull_ShouldThrowException() {
        Forecast forecast = new Forecast();
        ForecastDaily daily = new ForecastDaily();
        daily.setSunset(List.of(LocalDateTime.now()));
        forecast.setDaily(daily);

        assertThrows(WeatherException.class, () -> validatorService.validateForecast(forecast), ExceptionEnum.FORECAST_DAILY_SUNRISE_OR_SUNSET_NULL.getMessage());
    }

    @Test
    public void validateForecast_WhenSunsetIsNull_ShouldThrowException() {
        Forecast forecast = new Forecast();
        ForecastDaily daily = new ForecastDaily();
        daily.setSunrise(List.of(LocalDateTime.now()));
        forecast.setDaily(daily);

        assertThrows(WeatherException.class, () -> validatorService.validateForecast(forecast), ExceptionEnum.FORECAST_DAILY_SUNRISE_OR_SUNSET_NULL.getMessage());
    }

    @Test
    public void validateForecast_WhenPrecipitationSumIsNull_ShouldThrowException() {
        Forecast forecast = new Forecast();
        forecast.setDaily(new ForecastDaily().precipitationSum(null));

        assertThrows(WeatherException.class, () -> validatorService.validateForecast(forecast), ExceptionEnum.FORECAST_DAILY_PRECIPITATION_SUM_NULL.getMessage());
    }

    @Test
    public void validateForecast_WhenSunriseAndSunsetSizeNotEqual_ShouldThrowException() {
        Forecast forecast = new Forecast();
        ForecastDaily daily = new ForecastDaily();
        daily.setSunrise(List.of(LocalDateTime.now()));
        daily.setSunset(List.of(LocalDateTime.now(), LocalDateTime.now()));
        forecast.setDaily(daily);

        assertThrows(WeatherException.class, () -> validatorService.validateForecast(forecast), ExceptionEnum.FORECAST_COUNT_MISMATCH.getMessage());
    }
}