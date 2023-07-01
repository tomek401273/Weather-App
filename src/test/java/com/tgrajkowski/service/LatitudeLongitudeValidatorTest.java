package com.tgrajkowski.service;

import com.tgrajkowski.exception.ExceptionEnum;
import com.tgrajkowski.exception.WeatherException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LatitudeLongitudeValidatorTest {

    private LatitudeLongitudeValidator latitudeLongitudeValidator;

    @BeforeEach
    void setUp() {
        latitudeLongitudeValidator = new LatitudeLongitudeValidator();
    }

    @Test
    void shouldThrowExceptionWhenLatitudeIsNull() {
        WeatherException weatherException = assertThrows(WeatherException.class, () ->
                latitudeLongitudeValidator.validateRequestParameters(null, 50f));

        assertEquals(ExceptionEnum.LATITUDE_NULL, weatherException.getExceptionEnum());
    }

    @Test
    void shouldThrowExceptionWhenLatitudeIsTooLow() {
        WeatherException weatherException = assertThrows(WeatherException.class, () ->
                latitudeLongitudeValidator.validateRequestParameters(200f, 50f));

        assertEquals(ExceptionEnum.LATITUDE_OUT_OF_RANGE, weatherException.getExceptionEnum());
    }

    @Test
    void shouldThrowExceptionWhenLatitudeIsTooHigh() {
        WeatherException weatherException = assertThrows(WeatherException.class, () ->
                latitudeLongitudeValidator.validateRequestParameters(-200f, 50f));

        assertEquals(ExceptionEnum.LATITUDE_OUT_OF_RANGE, weatherException.getExceptionEnum());
    }

    @Test
    void shouldThrowExceptionWhenLongitudeIsNull() {
        WeatherException weatherException = assertThrows(WeatherException.class, () ->
                latitudeLongitudeValidator.validateRequestParameters(50f, null));

        assertEquals(ExceptionEnum.LONGITUDE_NULL, weatherException.getExceptionEnum());
    }

    @Test
    void shouldThrowExceptionWhenLongitudeIsTooLow() {
        WeatherException weatherException = assertThrows(WeatherException.class, () ->
                latitudeLongitudeValidator.validateRequestParameters(50f, 200f));

        assertEquals(ExceptionEnum.LONGITUDE_OUT_OF_RANGE, weatherException.getExceptionEnum());
    }

    @Test
    void shouldThrowExceptionWhenLongitudeIsTooHigh() {
        WeatherException weatherException = assertThrows(WeatherException.class, () ->
                latitudeLongitudeValidator.validateRequestParameters(50f, -200f));

        assertEquals(ExceptionEnum.LONGITUDE_OUT_OF_RANGE, weatherException.getExceptionEnum());
    }

    @Test
    void shouldNotThrowExceptionWhenValuesAreInRange() {
        assertDoesNotThrow(() ->
                latitudeLongitudeValidator.validateRequestParameters(50f, 50f));
    }
}
