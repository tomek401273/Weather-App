package com.tgrajkowski.controller;

import com.tgrajkowski.exception.ExceptionEnum;
import com.tgrajkowski.exception.WeatherException;
import com.tgrajkowski.model.dto.WeatherDto;
import com.tgrajkowski.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    private WeatherController weatherController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.weatherController = new WeatherController(weatherService);
    }

    @Test
    public void getWeather_WhenSuccessful_ShouldReturnOkStatus() {
        Float latitude = 45.123f;
        Float longitude = 12.123f;
        List<WeatherDto> weatherData = new ArrayList<>();
        when(weatherService.getWeatherData(latitude, longitude)).thenReturn(weatherData);

        assertDoesNotThrow(() -> weatherController.getWeather(latitude, longitude));
        verify(weatherService, times(1)).getWeatherData(latitude, longitude);
    }

    @Test
    public void getWeather_WhenWeatherApiError_ShouldThrowException() {
        Float latitude = 45.123f;
        Float longitude = 12.123f;
        when(weatherService.getWeatherData(latitude, longitude)).thenThrow(new WeatherException(ExceptionEnum.WEATHER_API_ERROR));

        assertThrows(WeatherException.class, () -> weatherController.getWeather(latitude, longitude));
        verify(weatherService, times(1)).getWeatherData(latitude, longitude);
    }

    @Test
    public void getWeather_WhenWeatherNotFound_ShouldThrowException() {
        Float latitude = 45.123f;
        Float longitude = 12.123f;
        when(weatherService.getWeatherData(latitude, longitude)).thenThrow(new WeatherException(ExceptionEnum.WEATHER_NOT_FOUND));

        assertThrows(WeatherException.class, () -> weatherController.getWeather(latitude, longitude));
        verify(weatherService, times(1)).getWeatherData(latitude, longitude);
    }
}
