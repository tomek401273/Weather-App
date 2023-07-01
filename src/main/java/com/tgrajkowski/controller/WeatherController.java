package com.tgrajkowski.controller;

import com.tgrajkowski.exception.WeatherExceptionDto;
import com.tgrajkowski.model.dto.WeatherDto;
import com.tgrajkowski.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherService weatherService;

    @Operation(method = "POST", summary = "Get weather data")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true, content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = WeatherDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = WeatherExceptionDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = WeatherExceptionDto.class)))
    })
    @GetMapping
    public List<WeatherDto> getWeather(
            @Parameter(in = ParameterIn.QUERY, description = "latitude", required = true, example = "52.52")
            @RequestParam Float latitude,
            @Parameter(in = ParameterIn.QUERY, description = "longitude", required = true, example = "13.41")
            @RequestParam Float longitude) {
        return weatherService.getWeatherData(latitude, longitude);
    }
}
