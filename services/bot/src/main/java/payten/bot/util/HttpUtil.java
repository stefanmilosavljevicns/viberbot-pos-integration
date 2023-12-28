package payten.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import payten.bot.model.OrderPOS;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static payten.bot.util.BotConstants.*;

@Component
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    @Autowired
    StringUtils stringUtils;
    private ArrayList<String> categories;
    public ArrayList<String> getCategories() {
        return categories;
    }

    ParameterizedTypeReference<ArrayList<OrderPOS>> responseType = new ParameterizedTypeReference<ArrayList<OrderPOS>>() {};

    public Boolean cartChecker(String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + checkIfUserCanOrder + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        Boolean status = Boolean.valueOf(responseEntity.getBody());
        logger.info(String.format(httpLogFormat, checkIfUserCanOrder, responseEntity.getStatusCode(), responseEntity.getBody()));
        return status;
    }

    public String checkIfTimeIsAvailable(LocalDateTime startDate, LocalDateTime endDate) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + checkTimeSlotAvailability + "?start=" + URLEncoder.encode(String.valueOf(startDate), StandardCharsets.UTF_8) + "&end=" + URLEncoder.encode(String.valueOf(endDate), StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        logger.info(String.format(httpLogFormat, checkTimeSlotAvailability, responseEntity.getStatusCode(), responseEntity.getBody()));
        return responseEntity.getBody();
    }

    public Double getTotalTime(String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + getTotalTime + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        Double time = Double.valueOf(Objects.requireNonNull(responseEntity.getBody()));
        logger.info(String.format(httpLogFormat, getTotalTime, responseEntity.getStatusCode(), responseEntity.getBody()));
        return time;

    }
    public ArrayList<OrderPOS> getHistoryOfOrders(String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + getHistoryOfReservations + "/" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<ArrayList<OrderPOS>> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, null, responseType);
        ArrayList<OrderPOS> orderList = responseEntity.getBody();
        logger.info(String.format(httpLogFormat, getHistoryOfReservations, responseEntity.getStatusCode(), responseEntity.getBody()));
        return orderList;
    }
    public String getUserLocale(String viberId) throws URISyntaxException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + userLocale + "/" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri,String.class);
        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        logger.info(String.format(httpLogFormat, userLocale, responseEntity.getStatusCode(), responseEntity.getBody()));
        return rootNode.path("customerLocale").asText();
    }
    public List<LocalDateTime> checkFreeTimeSlots(LocalDate time, int totalTime) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + checkFreeTimeSlots + "?localDate=" + URLEncoder.encode(String.valueOf(time), StandardCharsets.UTF_8) + "&totalMinutes=" + URLEncoder.encode(String.valueOf(totalTime), StandardCharsets.UTF_8));
        ResponseEntity<LocalDateTime[]> response = restTemplate.getForEntity(uri, LocalDateTime[].class);
        LocalDateTime[] dateTimeArray = response.getBody();
        List<LocalDateTime> freeTimeSlots = new ArrayList<>();
        if (dateTimeArray != null) {
            Collections.addAll(freeTimeSlots, dateTimeArray);
        }
        logger.info(String.format(httpLogFormat, checkFreeTimeSlots, response.getStatusCode(), Arrays.toString(response.getBody())));
        return freeTimeSlots;
    }
    public void changeUserLocale(String viberId,String locale) throws URISyntaxException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        URI uri = new URI(stringUtils.getRestAdress() + changeLocale + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8) + "&locale=" + URLEncoder.encode(locale, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri,requestEntity, String.class);
        logger.info(String.format(httpLogFormat, changeLocale, responseEntity.getStatusCode(), responseEntity.getBody()));
    }
    //In this endpoint we are sending order to POS and clearing cart for customer
    public void sendOrder(OrderPOS orderPOS, String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderPOS> requestEntity = new HttpEntity<>(orderPOS, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(stringUtils.getRestAdress() + addOrder, HttpMethod.POST, requestEntity, String.class);
        logger.info(String.format(httpLogFormat, addOrder, responseEntity.getStatusCode(), responseEntity.getBody()));
    }
        //Inserting selected service to DB in case User doesn't have field in DB here we create it.
    public void updateStartTime(String viberId,String startDate) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        URI uri = new URI(stringUtils.getRestAdress() + updateStartTime + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8) +"&startDate="+startDate);
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);        
        ResponseEntity<String> responseEntityPut = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, String.class);
    }

    public ArrayList<OrderPOS> get24HOrderPOS () {
        RestTemplate restTemplate = new RestTemplate();
        String endpointUrl = stringUtils.getRestAdress() + getOrdersWithin24Hourse;      
        ResponseEntity<ArrayList<OrderPOS>> responseEntity = restTemplate.exchange(endpointUrl, HttpMethod.GET, null, responseType);
        ArrayList<OrderPOS> orderList = responseEntity.getBody();
        return orderList;
    }

}

