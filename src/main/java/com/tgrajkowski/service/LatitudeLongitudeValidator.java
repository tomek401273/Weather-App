package com.tgrajkowski.service;

import com.tgrajkowski.exception.ExceptionEnum;
import com.tgrajkowski.exception.WeatherException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LatitudeLongitudeValidator {
    public void validateRequestParameters(Float latitude, Float longitude) {
        latitudeValidation(latitude);
        longitudeValidation(longitude);
    }

    private void latitudeValidation(Float latitude) {
        if (latitude == null) {
            log.error("Latitude is null");
            throw new WeatherException(ExceptionEnum.LATITUDE_NULL);
        }

        if (latitude < -90 || latitude > 90) {
            log.error("Latitude is out of range");
            throw new WeatherException(ExceptionEnum.LATITUDE_OUT_OF_RANGE);
        }
    }

    private void longitudeValidation(Float longitude) {
        if (longitude == null) {
            log.error("Longitude is null");
            throw new WeatherException(ExceptionEnum.LONGITUDE_NULL);
        }

        if (longitude < -180 || longitude > 180) {
            log.error("Longitude is out of range");
            throw new WeatherException(ExceptionEnum.LONGITUDE_OUT_OF_RANGE);
        }
    }
}
