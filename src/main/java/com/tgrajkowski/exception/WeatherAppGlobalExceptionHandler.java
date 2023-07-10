package com.tgrajkowski.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class WeatherAppGlobalExceptionHandler {
    private final Clock clock;

    @Autowired
    public WeatherAppGlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(WeatherException.class)
    protected ResponseEntity<WeatherExceptionDto> handleWeatherException(WeatherException ex) {
        log.error("WeatherException: {}", ex.getMessage());
        ExceptionEnum exceptionEnum = ex.getExceptionEnum();
        return ResponseEntity.status(exceptionEnum.getHttpStatus())
                .body(buildWeatherExceptionDto(exceptionEnum));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<WeatherExceptionDto> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        log.error("MissingServletRequestParameterException: {}", ex.getMessage());
        ExceptionEnum exceptionEnum = ExceptionEnum.MISSING_SERVLET_REQUEST_PARAMETER;
        return new ResponseEntity<>(buildWeatherExceptionDto(exceptionEnum), exceptionEnum.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<WeatherExceptionDto> handleArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("MissingServletRequestParameterException: {}", ex.getMessage());
        ExceptionEnum methodArgumentTypeMismatch = ExceptionEnum.METHOD_ARGUMENT_TYPE_MISMATCH;
        return new ResponseEntity<>(buildWeatherExceptionDto(methodArgumentTypeMismatch), methodArgumentTypeMismatch.getHttpStatus());
    }

    @ExceptionHandler
    protected ResponseEntity<WeatherExceptionDto> handleException(Exception ex) {
        log.error("Exception: {}", ex.getMessage());
        ExceptionEnum exceptionEnum = ExceptionEnum.WEATHER_API_ERROR;
        return ResponseEntity.status(exceptionEnum.getHttpStatus())
                .body(buildWeatherExceptionDto(exceptionEnum));
    }

    private WeatherExceptionDto buildWeatherExceptionDto(ExceptionEnum exceptionEnum) {
        return new WeatherExceptionDto(exceptionEnum.getCode(), exceptionEnum.getMessage(), LocalDateTime.now(clock).toString());
    }
}
