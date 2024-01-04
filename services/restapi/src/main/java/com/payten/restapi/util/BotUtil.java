package com.payten.restapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
@Component
public class BotUtil {


    @Value("viber.bot-path-accept")
    private String pathAccept;
    @Value("viber.bot-path-accept")
    private String pathDecline;
    private String pathUpdate;
    @Value("viber.bot-adress")
    private String rootPath;
    public void notifyUserForChangeOfReservation(String viberId,String startDate) throws URISyntaxException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        URI uri = new URI(rootPath + pathUpdate + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8) + "&startDate=" + URLEncoder.encode(startDate, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT,requestEntity,String.class);
    }
    public void notifyUserForAcceptingReservation(String viberId) throws URISyntaxException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        URI uri = new URI(rootPath + pathAccept + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT,requestEntity,String.class);
    }
    public void notifyUserForDecliningReservation(String viberId) throws URISyntaxException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        URI uri = new URI(rootPath + pathDecline + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT,requestEntity,String.class);
    }

}
