package com.cema.activity.services.client.economic.impl;

import com.cema.activity.domain.ErrorResponse;
import com.cema.activity.domain.economic.SupplyOperation;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.economic.EconomicClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class EconomicClientServiceImpl implements EconomicClientService {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PATH_VALIDATE_SUPPLY = "supply/validate/{name}";
    private static final String PATH_REGISTER_SUPPLY_OPERATION = "operation/supply/";

    private final RestTemplate restTemplate;
    private final String url;
    private final AuthorizationService authorizationService;
    private final ObjectMapper mapper = new ObjectMapper();

    public EconomicClientServiceImpl(RestTemplate restTemplate, @Value("${back-end.economic.url}") String url, AuthorizationService authorizationService) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.authorizationService = authorizationService;
    }

    @SneakyThrows
    @Override
    public void validateSupply(String food) {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_VALIDATE_SUPPLY;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}", httpHeaders);
        try {
            restTemplate.exchange(searchUrl, HttpMethod.GET, entity, Object.class, food);
        } catch (RestClientResponseException httpClientErrorException) {
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }

    @Override
    @SneakyThrows
    public void registerSupplyOperation(SupplyOperation supplyOperation){
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_REGISTER_SUPPLY_OPERATION;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SupplyOperation> entity = new HttpEntity<>(supplyOperation, httpHeaders);
        try {
            restTemplate.exchange(searchUrl, HttpMethod.POST, entity, SupplyOperation.class);
        } catch (RestClientResponseException httpClientErrorException) {
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }
}
