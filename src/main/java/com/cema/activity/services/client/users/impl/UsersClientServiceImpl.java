package com.cema.activity.services.client.users.impl;

import com.cema.activity.domain.ErrorResponse;
import com.cema.activity.domain.security.User;
import com.cema.activity.exceptions.ValidationException;
import com.cema.activity.services.authorization.AuthorizationService;
import com.cema.activity.services.client.users.UsersClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class UsersClientServiceImpl implements UsersClientService {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PATH_USER = "users/{username}";

    private final RestTemplate restTemplate;
    private final String url;
    private final AuthorizationService authorizationService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UsersClientServiceImpl(RestTemplate restTemplate, @Value("${back-end.users.url}") String url,
                                  AuthorizationService authorizationService) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.authorizationService = authorizationService;
    }

    @SneakyThrows
    @Override
    public User getUser(String userName) {
        String authToken = authorizationService.getUserAuthToken();
        String searchUrl = url + PATH_USER;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, authToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity("{}", httpHeaders);
        try {
            ResponseEntity<User> response = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, User.class, userName);
            return response.getBody();
        } catch (RestClientResponseException httpClientErrorException) {
            String response = httpClientErrorException.getResponseBodyAsString();
            ErrorResponse errorResponse = mapper.readValue(response, ErrorResponse.class);
            throw new ValidationException(errorResponse.getMessage(), httpClientErrorException);
        }
    }
}
