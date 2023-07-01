package com.tgrajkowski.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    WEATHER_NOT_FOUND("WEATHER_NOT_FOUND", "Weather not found", HttpStatus.NOT_FOUND),
    WEATHER_BAD_REQUEST("WEATHER_BAD_REQUEST","Weather bad request" , HttpStatus.BAD_REQUEST),
    WEATHER_EXTERNAL_API_ERROR("WEATHER_EXTERNAL_API_ERROR", "Weather external API error", HttpStatus.INTERNAL_SERVER_ERROR),
    WEATHER_API_ERROR("WEATHER_API_ERROR", "Weather API error", HttpStatus.INTERNAL_SERVER_ERROR),
    FORECAST_NULL("FORECAST_NULL", "Forecast object is null", HttpStatus.INTERNAL_SERVER_ERROR),
    FORECAST_DAILY_NULL("FORECAST_DAILY_NULL", "Daily forecast data is null", HttpStatus.INTERNAL_SERVER_ERROR),
    FORECAST_DAILY_SUNRISE_OR_SUNSET_NULL("FORECAST_DAILY_SUNRISE_OR_SUNSET_NULL", "Sunrise or Sunset data in Daily forecast is null", HttpStatus.INTERNAL_SERVER_ERROR),
    FORECAST_COUNT_MISMATCH("FORECAST_COUNT_MISMATCH", "Mismatch in the count of data", HttpStatus.INTERNAL_SERVER_ERROR),
    FORECAST_DAILY_PRECIPITATION_SUM_NULL("FORECAST_DAILY_PRECIPITATION_SUM_NULL","Precipitation data in Daily forecast is null" , HttpStatus.INTERNAL_SERVER_ERROR ),
    LATITUDE_OUT_OF_RANGE("LATITUDE_OUT_OF_RANGE","Latitude should be in range from -90 to 90" , HttpStatus.BAD_REQUEST),
    LONGITUDE_OUT_OF_RANGE("LONGITUDE_OUT_OF_RANGE", "Longitude should be in range from -180 to 180",HttpStatus.BAD_REQUEST),
    LATITUDE_NULL("LATITUDE_NULL","Latitude should be present" , HttpStatus.BAD_REQUEST),
    LONGITUDE_NULL("LONGITUDE_NULL","Longitude should be present" , HttpStatus.BAD_REQUEST),
    METHOD_ARGUMENT_TYPE_MISMATCH("METHOD_ARGUMENT_TYPE_MISMATCH", "Method argument type mismatch", HttpStatus.BAD_REQUEST),
    MISSING_SERVLET_REQUEST_PARAMETER("MISSING_SERVLET_REQUEST_PARAMETER", "Missing servlet request parameter", HttpStatus.BAD_REQUEST),


    FORECAST_DATE_SUNRISE_OR_SUNSET_NOT_EQUAL("FORECAST_DATE_SUNRISE_OR_SUNSET_NOT_EQUAL", "Date in PrecipitationDate object is not equal to Sunrise or Sunset date", HttpStatus.INTERNAL_SERVER_ERROR);

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
