package com.tgrajkowski.service;

import com.tgrajkowski.com.mycompany.app.model.Forecast;
import com.tgrajkowski.model.dto.WeatherDto;
import com.tgrajkowski.model.entity.RequestParameters;
import com.tgrajkowski.service.repository.RequestParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private final ForecastService forecastService;
    private final RequestParametersRepository requestParametersRepository;
    private final ValidatorService validatorService;
    private final Clock clock;
    private final LatitudeLongitudeValidator latitudeLongitudeValidator;

    @Autowired
    public WeatherService(ForecastService forecastService, RequestParametersRepository requestParametersRepository, ValidatorService validatorService, Clock clock, LatitudeLongitudeValidator latitudeLongitudeValidator) {
        this.forecastService = forecastService;
        this.requestParametersRepository = requestParametersRepository;
        this.validatorService = validatorService;
        this.clock = clock;
        this.latitudeLongitudeValidator = latitudeLongitudeValidator;
    }

    public List<WeatherDto> getWeatherData(float latitude, float longitude) {
        latitudeLongitudeValidator.validateRequestParameters(latitude, longitude);
        requestParametersRepository.save(new RequestParameters(latitude, longitude, LocalDateTime.now(clock)));
        Forecast forecast = forecastService.getForecast(latitude, longitude);
        validatorService.validateForecast(forecast);

        return buildPrecipitationDateList(forecast);
    }

    private List<WeatherDto> buildPrecipitationDateList(Forecast forecast) {
        List<Double> precipitationList = forecast.getDaily().getPrecipitationSum();
        List<LocalDateTime> sunrise = forecast.getDaily().getSunrise();
        List<LocalDateTime> sunset = forecast.getDaily().getSunset();

        List<WeatherDto> precipitationDateList = new ArrayList<>();
        for (int i = 0; i < precipitationList.size(); i++) {
            precipitationDateList.add(WeatherDto.builder()
                    .precipitation(BigDecimal.valueOf(precipitationList.get(i)))
                    .sunrise(sunrise.get(i))
                    .sunset(sunset.get(i))
                    .build());
        }
        return precipitationDateList;
    }
}
