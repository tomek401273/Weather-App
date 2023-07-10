package com.tgrajkowski.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tgrajkowski.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.util.List;

@Configuration
public class CoreConfiguration {
    @Value("${open.meteo.address}")
    private String address;

    @Bean
    public Clock clock() {
        return new ApplicationClock();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ApiClient apiClient(RestTemplate restTemplate) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(address);
        return apiClient;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
