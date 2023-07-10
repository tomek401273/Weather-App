package com.tgrajkowski.service;

import com.tgrajkowski.com.mycompany.app.model.Forecast;
import com.tgrajkowski.com.mycompany.app.model.ForecastDaily;
import com.tgrajkowski.data.TestClock;
import com.tgrajkowski.exception.ExceptionEnum;
import com.tgrajkowski.exception.WeatherException;
import com.tgrajkowski.model.dto.WeatherDto;
import com.tgrajkowski.model.entity.RequestParameters;
import com.tgrajkowski.service.repository.RequestParametersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {
    @Mock
    ForecastService forecastService;

    @Mock
    RequestParametersRepository requestParametersRepository;

    @Mock
    ValidatorService validatorService;

    @Mock
    LatitudeLongitudeValidator latitudeLongitudeValidator;

    WeatherService weatherService;

    TestClock clock;

    @BeforeEach
    void setUp() {
        clock = new TestClock("2022-09-23T20:00:00.000000Z");
        MockitoAnnotations.openMocks(this);
        weatherService = new WeatherService(forecastService, requestParametersRepository, validatorService, clock, latitudeLongitudeValidator);
    }

    private final LocalDateTime fixedDateTime = LocalDateTime.of(2023, 7, 8, 0, 0);

    private Forecast buildForecast() {
        Forecast forecast = new Forecast();

        forecast.setDaily(new ForecastDaily()
                .precipitationSum(Arrays.asList(1.0, 2.0))
                .sunrise(Arrays.asList(fixedDateTime.plusHours(6), fixedDateTime.plusDays(1).plusHours(6)))
                .sunset(Arrays.asList(fixedDateTime.plusHours(18), fixedDateTime.plusDays(1).plusHours(18))));
        return forecast;
    }

    @Test
    void getWeatherData() {
        float latitude = 123.456f;
        float longitude = 789.012f;

        Forecast forecast = buildForecast();

        when(forecastService.getForecast(latitude, longitude)).thenReturn(forecast);

        List<WeatherDto> result = weatherService.getWeatherData(latitude, longitude);

        verify(requestParametersRepository, times(1)).save(any(RequestParameters.class));
        verify(forecastService, times(1)).getForecast(latitude, longitude);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(forecast.getDaily().getPrecipitationSum().get(0), result.get(0).getPrecipitation().doubleValue());
        assertEquals(forecast.getDaily().getSunrise().get(0).toString(), result.get(0).getSunrise().toString());
        assertEquals(forecast.getDaily().getSunset().get(0).toString(), result.get(0).getSunset().toString());

        assertEquals(forecast.getDaily().getPrecipitationSum().get(1), result.get(1).getPrecipitation().doubleValue());
        assertEquals(forecast.getDaily().getSunrise().get(1).toString(), result.get(1).getSunrise().toString());
        assertEquals(forecast.getDaily().getSunset().get(1).toString(), result.get(1).getSunset().toString());
    }

    @Test
    void testForecastServiceThrowsException() {
        float latitude = 123.456f;
        float longitude = 789.012f;

        when(forecastService.getForecast(latitude, longitude)).thenThrow(new WeatherException(ExceptionEnum.WEATHER_API_ERROR));

        Exception exception = assertThrows(WeatherException.class, () -> weatherService.getWeatherData(latitude, longitude));
        assertEquals("Weather API error", exception.getMessage());
    }

    @Test
    void testRequestParametersRepositoryThrowsException() {
        float latitude = 123.456f;
        float longitude = 789.012f;

        doThrow(new DataAccessException("Error in DB") {
        }).when(requestParametersRepository).save(any(RequestParameters.class));

        Exception exception = assertThrows(DataAccessException.class, () -> weatherService.getWeatherData(latitude, longitude));
        assertEquals("Error in DB", exception.getMessage());
    }

    @Test
    void testForecastDataIsNull() {
        float latitude = 123.456f;
        float longitude = 789.012f;

        when(forecastService.getForecast(latitude, longitude)).thenReturn(null);
        doThrow(new WeatherException(ExceptionEnum.FORECAST_NULL)).when(validatorService).validateForecast(any());

        Exception exception = assertThrows(WeatherException.class, () -> weatherService.getWeatherData(latitude, longitude));
        assertEquals(ExceptionEnum.FORECAST_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testLatitudeLongitudeValidator() {
        float latitude = 123.456f;
        float longitude = 789.012f;

        doThrow(new WeatherException(ExceptionEnum.LATITUDE_NULL)).when(latitudeLongitudeValidator).validateRequestParameters(latitude, longitude);

        WeatherException weatherException = assertThrows(WeatherException.class, () -> weatherService.getWeatherData(latitude, longitude));
        verify(latitudeLongitudeValidator, times(1)).validateRequestParameters(latitude, longitude);
        verify(requestParametersRepository, times(0)).save(any(RequestParameters.class));
        verify(forecastService, times(0)).getForecast(latitude, longitude);
        verify(validatorService, times(0)).validateForecast(any());
        assertEquals(ExceptionEnum.LATITUDE_NULL, weatherException.getExceptionEnum());
    }
}
