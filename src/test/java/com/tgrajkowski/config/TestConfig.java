package com.tgrajkowski.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.tgrajkowski.com.mycompany.app.api.DefaultApi;
import com.tgrajkowski.data.TestClock;
import com.tgrajkowski.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;

@Configuration
@ActiveProfiles("test")
public class TestConfig {

    @Value("${open.meteo.address}")
    private String address;

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WireMockServer wireMockServer() {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort().dynamicPort());
        wireMockServer.start();
        return wireMockServer;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    @Primary
    public ApiClient apiClient(RestTemplate restTemplate, WireMockServer wireMockServer) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(address + ":" + wireMockServer.port());
        return apiClient;
    }

    @Bean
    @Primary
    public DefaultApi defaultApi(ApiClient apiClient) {
        return new DefaultApi(apiClient);
    }

    @Bean
    @Primary
    public Clock clock() {
        return new TestClock("2022-09-23T20:00:00.000000Z");
    }
}
