package com.tgrajkowski.service;

import com.tgrajkowski.com.mycompany.app.api.DefaultApi;
import com.tgrajkowski.com.mycompany.app.model.Forecast;
import com.tgrajkowski.configuration.WeatherProperties;
import com.tgrajkowski.data.TestClock;
import com.tgrajkowski.exception.ExceptionEnum;
import com.tgrajkowski.exception.WeatherException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForecastServiceTest {
    private final List<String> dailyParameters = List.of("sunset", "sunrise", "precipitation_sum");
    private final String timezone = "Europe/London";
    @Mock
    private DefaultApi defaultApi;

    private Clock clock;

    private ForecastService forecastService;

    @Mock
    private WeatherProperties weatherProperties;

    @BeforeEach
    void setUp() {
        clock = new TestClock("2022-09-23T20:00:00.000000Z");
        MockitoAnnotations.openMocks(this);
        forecastService = new ForecastService(weatherProperties, defaultApi, clock);
    }

    @Test
    void getForecast_Success() {
        // given
        LocalDate startDate = LocalDate.now(clock).minusDays(7);
        LocalDate endDate = LocalDate.now(clock).minusDays(1);

        Forecast expectedForecast = new Forecast();
        when(defaultApi.getForecast(anyFloat(), anyFloat(), anyList(), anyString(), any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(expectedForecast);
        when(weatherProperties.getDailyParameters()).thenReturn(dailyParameters);
        when(weatherProperties.getTimezone()).thenReturn(timezone);

        // when
        Forecast actualForecast = forecastService.getForecast(0.0f, 0.0f);

        // then
        verify(defaultApi).getForecast(0.0f, 0.0f,
                List.of("sunset", "sunrise", "precipitation_sum"), "Europe/London", startDate, endDate);
        assertEquals(expectedForecast, actualForecast);
    }


    @Test
    void getForecast_WeatherNotFoundException() {
        // given
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        when(defaultApi.getForecast(anyFloat(), anyFloat(), anyList(), anyString(), any(LocalDate.class),
                any(LocalDate.class)))
                .thenThrow(exception);
        when(weatherProperties.getDailyParameters()).thenReturn(dailyParameters);
        when(weatherProperties.getTimezone()).thenReturn(timezone);

        // when
        WeatherException weatherException = assertThrows(WeatherException.class,
                () -> forecastService.getForecast(0.0f, 0.0f));

        // then
        assertEquals(ExceptionEnum.WEATHER_NOT_FOUND, weatherException.getExceptionEnum());
        assertEquals(exception, weatherException.getCause());
    }

    @Test
    void getForecast_ApiErrorException() {
        // given
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        when(weatherProperties.getDailyParameters()).thenReturn(dailyParameters);
        when(weatherProperties.getTimezone()).thenReturn(timezone);
        when(defaultApi.getForecast(anyFloat(), anyFloat(), anyList(), anyString(), any(LocalDate.class),
                any(LocalDate.class)))
                .thenThrow(exception);

        // when
        WeatherException weatherException = assertThrows(WeatherException.class,
                () -> forecastService.getForecast(0.0f, 0.0f));

        // then
        assertEquals(ExceptionEnum.WEATHER_BAD_REQUEST, weatherException.getExceptionEnum());
        assertEquals(exception, weatherException.getCause());
    }

    @Test
    void getForecast_RestClientException() {
        // given
        RestClientException exception = new RestClientException("API error");
        when(weatherProperties.getDailyParameters()).thenReturn(dailyParameters);
        when(weatherProperties.getTimezone()).thenReturn(timezone);
        when(defaultApi.getForecast(anyFloat(), anyFloat(), anyList(), anyString(), any(LocalDate.class),
                any(LocalDate.class)))
                .thenThrow(exception);

        // when
        WeatherException weatherException = assertThrows(WeatherException.class,
                () -> forecastService.getForecast(0.0f, 0.0f));

        // then
        assertEquals(ExceptionEnum.WEATHER_EXTERNAL_API_ERROR, weatherException.getExceptionEnum());
        assertEquals(exception, weatherException.getCause());
    }

    @Test
    void getForecast_GenericException() {
        // given
        RuntimeException exception = new RuntimeException("Internal server error");
        when(weatherProperties.getDailyParameters()).thenReturn(dailyParameters);
        when(weatherProperties.getTimezone()).thenReturn(timezone);
        when(defaultApi.getForecast(anyFloat(), anyFloat(), anyList(), anyString(), any(LocalDate.class),
                any(LocalDate.class)))
                .thenThrow(exception);

        // when
        WeatherException weatherException = assertThrows(WeatherException.class,
                () -> forecastService.getForecast(0.0f, 0.0f));

        // then
        assertEquals(ExceptionEnum.WEATHER_API_ERROR, weatherException.getExceptionEnum());
        assertEquals(exception, weatherException.getCause());
    }
}
