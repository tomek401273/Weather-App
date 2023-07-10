package com.tgrajkowski.service;

import com.tgrajkowski.com.mycompany.app.api.DefaultApi;
import com.tgrajkowski.com.mycompany.app.model.Forecast;
import com.tgrajkowski.configuration.WeatherProperties;
import com.tgrajkowski.exception.ExceptionEnum;
import com.tgrajkowski.exception.WeatherException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.time.Clock;
import java.time.LocalDate;

@Slf4j
@Service
public class ForecastService {
    private final WeatherProperties weatherProperties;
    private final DefaultApi defaultApi;

    private final Clock clock;

    public ForecastService(WeatherProperties weatherProperties, DefaultApi defaultApi, Clock clock) {
        this.weatherProperties = weatherProperties;
        this.defaultApi = defaultApi;
        this.clock = clock;
    }

    public Forecast getForecast(float latitude, float longitude) {
        LocalDate startDate = LocalDate.now(clock).minusDays(7);
        LocalDate endDate = LocalDate.now(clock).minusDays(1);

        try {
            return defaultApi.getForecast(latitude, longitude, weatherProperties.getDailyParameters(), weatherProperties.getTimezone(), startDate, endDate);
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                log.error("HttpClientErrorException.NotFound: " + e.getMessage());
                throw new WeatherException(ExceptionEnum.WEATHER_NOT_FOUND, e);
            }
            if (HttpStatus.BAD_REQUEST.equals(e.getStatusCode())) {
                log.error("HttpClientErrorException.BadRequest: " + e.getMessage());
                throw new WeatherException(ExceptionEnum.WEATHER_BAD_REQUEST, e);
            }

            log.error("HttpClientErrorException: " + e.getMessage());
            throw new WeatherException(ExceptionEnum.WEATHER_EXTERNAL_API_ERROR, e);
        } catch (RestClientException e) {
            log.error("RestClientException: " + e.getMessage());
            throw new WeatherException(ExceptionEnum.WEATHER_EXTERNAL_API_ERROR, e);
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
            throw new WeatherException(ExceptionEnum.WEATHER_API_ERROR, e);
        }
    }
}
