package com.tgrajkowski.exception;

import lombok.Getter;
@Getter
public class WeatherExceptionDto {
    private final String code;
    private final String message;
    private final String date;

    public WeatherExceptionDto(String code, String message, String date) {
        this.code = code;
        this.message = message;
        this.date = date;
    }
}
