package com.tgrajkowski.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.tgrajkowski.WeatherAppApplication;
import com.tgrajkowski.com.mycompany.app.api.DefaultApi;
import com.tgrajkowski.com.mycompany.app.model.Forecast;
import com.tgrajkowski.com.mycompany.app.model.ForecastDaily;
import com.tgrajkowski.com.mycompany.app.model.GetForecast400Response;
import com.tgrajkowski.config.TestConfig;
import com.tgrajkowski.configuration.WeatherProperties;
import com.tgrajkowski.exception.ExceptionEnum;
import com.tgrajkowski.model.dto.WeatherDto;
import com.tgrajkowski.model.entity.RequestParameters;
import com.tgrajkowski.service.WeatherService;
import com.tgrajkowski.service.repository.RequestParametersRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = {WeatherAppApplication.class, TestConfig.class})
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class WeatherControllerIntegrationTest {
    private static final String BASE_URL = "/weather";
    private static final String URL_FORMAT = BASE_URL + "?latitude=%s&longitude=%s";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private DefaultApi defaultApi;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private RequestParametersRepository requestParametersRepository;

    @Autowired
    private WeatherProperties weatherProperties;

    @Autowired
    private Clock clock;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private final LocalDateTime fixedDateTime = LocalDateTime.of(2023, 7, 8, 0, 0);


    @Test
    public void getWeather_WhenSuccessful_ShouldReturnOkStatus() throws Exception {
        // given
        Float latitude = 45.123f;
        Float longitude = 12.123f;
        String url = String.format(URL_FORMAT, latitude, longitude);
        Forecast forecast = buildForecast();

        List<WeatherDto> weatherData = new ArrayList<>();
        weatherData.add(WeatherDto
                .builder()
                .precipitation(BigDecimal.valueOf(forecast.getDaily().getPrecipitationSum().get(0)))
                .sunrise(forecast.getDaily().getSunrise().get(0))
                .sunset(forecast.getDaily().getSunset().get(0))
                .build());

        weatherData.add(WeatherDto.builder()
                .precipitation(BigDecimal.valueOf(forecast.getDaily().getPrecipitationSum().get(1)))
                .sunrise(forecast.getDaily().getSunrise().get(1))
                .sunset(forecast.getDaily().getSunset().get(1))
                .build());

        String body = objectMapper.writeValueAsString(forecast);
        String mockUrl = String.format("/forecast?latitude=%s&longitude=%s&daily=%s&daily=%s&daily=%s&timezone=%s&start_date=%s&end_date=%s",
                latitude, longitude,
                weatherProperties.getDailyParameters().get(0),
                weatherProperties.getDailyParameters().get(1),
                weatherProperties.getDailyParameters().get(2),
                weatherProperties.getTimezone(),
                LocalDate.now(clock).minusDays(7),
                LocalDate.now(clock).minusDays(1));

        wireMockServer.stubFor(WireMock.get(mockUrl)
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
        // when & then
        this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].precipitation", is(weatherData.get(0).getPrecipitation().doubleValue()))) // assert first element
                .andExpect(jsonPath("$[0].sunrise", is(weatherData.get(0).getSunrise().toString())))
                .andExpect(jsonPath("$[0].sunset", is(weatherData.get(0).getSunset().toString())))
                .andExpect(jsonPath("$[1].precipitation", is(weatherData.get(1).getPrecipitation().doubleValue()))) // assert second element
                .andExpect(jsonPath("$[1].sunrise", is(weatherData.get(1).getSunrise().toString())))
                .andExpect(jsonPath("$[1].sunset", is(weatherData.get(1).getSunset().toString())));
        List<RequestParameters> requestParameters = requestParametersRepository.findAll();
        assertThat(requestParameters).hasSize(1);
        assertThat(requestParameters.get(0).getLatitude()).isEqualTo(latitude);
        assertThat(requestParameters.get(0).getLongitude()).isEqualTo(longitude);
        assertThat(requestParameters.get(0).getDate()).isEqualTo(LocalDateTime.now(clock));

        // clean
        cleanUp();
    }

    @Test
    public void getWeather_WhenBadRequest_ShouldReturnExternalApiError() throws Exception {
        // given
        Float latitude = 45.123f;
        Float longitude = 12.123f;
        String url = String.format(URL_FORMAT, latitude, longitude);

        String body = objectMapper.writeValueAsString(new GetForecast400Response()
                .error(true)
                .reason("Bad request"));
        wireMockServer.stubFor(WireMock.get(WireMock.anyUrl())
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));
        ExceptionEnum exceptionEnum = ExceptionEnum.WEATHER_BAD_REQUEST;
        // when & then
        this.mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is(exceptionEnum.getCode())))
                .andExpect(jsonPath("$.message", is(exceptionEnum.getMessage())))
                .andExpect(jsonPath("$.date", Matchers.notNullValue()));

        List<RequestParameters> requestParameters = requestParametersRepository.findAll();
        assertThat(requestParameters).hasSize(1);
        assertThat(requestParameters.get(0).getLatitude()).isEqualTo(latitude);
        assertThat(requestParameters.get(0).getLongitude()).isEqualTo(longitude);

        // clean
        cleanUp();
    }

    @Test
    public void getWeather_When500Request_ShouldReturnExternalApiError() throws Exception {
        // given
        Float latitude = 45.123f;
        Float longitude = 12.123f;
        String url = String.format(URL_FORMAT, latitude, longitude);
        wireMockServer.stubFor(WireMock.get(WireMock.anyUrl())
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withHeader("Content-Type", "application/json")));
        ExceptionEnum exceptionEnum = ExceptionEnum.WEATHER_EXTERNAL_API_ERROR;

        // when & then
        this.mockMvc.perform(get(url))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is(exceptionEnum.getCode())))
                .andExpect(jsonPath("$.message", is(exceptionEnum.getMessage())))
                .andExpect(jsonPath("$.date", Matchers.notNullValue()));

        List<RequestParameters> requestParameters = requestParametersRepository.findAll();
        assertThat(requestParameters).hasSize(1);
        assertThat(requestParameters.get(0).getLatitude()).isEqualTo(latitude);
        assertThat(requestParameters.get(0).getLongitude()).isEqualTo(longitude);

        // clean
        cleanUp();
    }

    @Test
    public void getWeather_When400Request_ShouldReturnWrongParameterTypesApiError() throws Exception {
        // given
        Float latitude = null;
        Float longitude = null;
        String url = String.format(URL_FORMAT, latitude, longitude);
        wireMockServer.stubFor(WireMock.get(WireMock.anyUrl())
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withHeader("Content-Type", "application/json")));
        ExceptionEnum exceptionEnum = ExceptionEnum.METHOD_ARGUMENT_TYPE_MISMATCH;

        // when & then
        this.mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is(exceptionEnum.getCode())))
                .andExpect(jsonPath("$.message", is(exceptionEnum.getMessage())))
                .andExpect(jsonPath("$.date", Matchers.notNullValue()));

        List<RequestParameters> requestParameters = requestParametersRepository.findAll();
        assertThat(requestParameters).isEmpty();
    }

    @Test
    public void getWeather_When400Request_ShouldReturnParameterIsNotPresentApiError() throws Exception {
        // given
        wireMockServer.stubFor(WireMock.get(WireMock.anyUrl())
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withHeader("Content-Type", "application/json")));
        ExceptionEnum exceptionEnum = ExceptionEnum.MISSING_SERVLET_REQUEST_PARAMETER;

        // when & then
        this.mockMvc.perform(get(BASE_URL))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is(exceptionEnum.getCode())))
                .andExpect(jsonPath("$.message", is(exceptionEnum.getMessage())))
                .andExpect(jsonPath("$.date", Matchers.notNullValue()));

        List<RequestParameters> requestParameters = requestParametersRepository.findAll();
        assertThat(requestParameters).isEmpty();
    }

    @Test
    public void getWeather_When400Request_ShouldReturnParameterOutOfRangeApiError() throws Exception {
        // given
        Float latitude = -100f;
        Float longitude = 300f;
        String url = String.format(URL_FORMAT, latitude, longitude);
        wireMockServer.stubFor(WireMock.get(WireMock.anyUrl())
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withHeader("Content-Type", "application/json")));
        ExceptionEnum exceptionEnum = ExceptionEnum.LATITUDE_OUT_OF_RANGE;

        // when & then
        this.mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.code", is(exceptionEnum.getCode())))
                .andExpect(jsonPath("$.message", is(exceptionEnum.getMessage())))
                .andExpect(jsonPath("$.date", Matchers.notNullValue()));

        List<RequestParameters> requestParameters = requestParametersRepository.findAll();
        assertThat(requestParameters).isEmpty();
    }

    private Forecast buildForecast() {
        Forecast forecast = new Forecast();
        forecast.setTimezone("Logic2000");

        forecast.setDaily(new ForecastDaily()
                .precipitationSum(Arrays.asList(1.0, 2.0))
                .sunrise(Arrays.asList(fixedDateTime.plusHours(6), fixedDateTime.plusDays(1).plusHours(6)))
                .sunset(Arrays.asList(fixedDateTime.plusHours(18), fixedDateTime.plusDays(1).plusHours(18))));
        return forecast;
    }

    private void cleanUp() {
        requestParametersRepository.deleteAll();
    }
}
