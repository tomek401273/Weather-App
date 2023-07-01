package com.tgrajkowski.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class WeatherProperties {
    @Value("#{'${weather.daily.parameters}'.split(',')}")
    private List<String> dailyParameters;

    @Value("${weather.timezone}")
    private String timezone;
}
