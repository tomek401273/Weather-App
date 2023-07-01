package com.tgrajkowski.service;

import com.tgrajkowski.com.mycompany.app.model.Forecast;
import com.tgrajkowski.exception.ExceptionEnum;
import com.tgrajkowski.exception.WeatherException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ValidatorService {

    public void validateForecast(Forecast forecast) {
        if (forecast == null) {
            log.error("Forecast is null");
            throw new WeatherException(ExceptionEnum.FORECAST_NULL);
        }

        if (forecast.getDaily() == null) {
            log.error("Forecast daily is null");
            throw new WeatherException(ExceptionEnum.FORECAST_DAILY_NULL);
        }

        if (forecast.getDaily().getPrecipitationSum() == null) {
            log.error("Forecast daily precipitationSum is null");
            throw new WeatherException(ExceptionEnum.FORECAST_DAILY_PRECIPITATION_SUM_NULL);
        }

        if (forecast.getDaily().getSunrise() == null || forecast.getDaily().getSunset() == null) {
            log.error("Forecast daily sunrise or sunset is null");
            throw new WeatherException(ExceptionEnum.FORECAST_DAILY_SUNRISE_OR_SUNSET_NULL);
        }

        if (forecast.getDaily().getSunrise().size() != forecast.getDaily().getSunset().size()) {
            log.error("Forecast daily sunrise and sunset count mismatch");
            throw new WeatherException(ExceptionEnum.FORECAST_COUNT_MISMATCH);
        }

        // TODO - add more validation for precipitationSum
    }

//    public void validateDates(List<PrecipitationDate> precipitationDateList, List<LocalDateTime> sunriseList, List<LocalDateTime> sunsetList) {
//        if (precipitationDateList.size() != sunriseList.size() || precipitationDateList.size() != sunsetList.size()) {
//            throw new WeatherException(ExceptionEnum.FORECAST_COUNT_MISMATCH);
//        }
//        for (int i = 0; i <precipitationDateList.size(); i++) {
//            if(!precipitationDateList.get(i).getDate().isEqual(sunriseList.get(i).toLocalDate()) || !precipitationDateList.get(i).getDate().isEqual(sunsetList.get(i).toLocalDate())) {
//                throw new WeatherException(ExceptionEnum.FORECAST_DATE_SUNRISE_OR_SUNSET_NOT_EQUAL);
//            }
//        }
//    }
}
