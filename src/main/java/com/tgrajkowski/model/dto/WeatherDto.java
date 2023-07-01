package com.tgrajkowski.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class WeatherDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime sunrise;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime sunset;

    private BigDecimal precipitation;
}
