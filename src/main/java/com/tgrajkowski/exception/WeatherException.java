package com.tgrajkowski.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class WeatherException extends RuntimeException {
    private final ExceptionEnum exceptionEnum;
    private final Map<String, String> additionalInfo;

    public WeatherException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.exceptionEnum = exceptionEnum;
        this.additionalInfo = new HashMap<>();
    }

    public WeatherException(ExceptionEnum exceptionEnum, Throwable cause) {
        super(cause);
        this.exceptionEnum = exceptionEnum;
        this.additionalInfo = new HashMap<>();
    }

    @Override
    public String getMessage() {
        return exceptionEnum.getMessage();
    }

}
