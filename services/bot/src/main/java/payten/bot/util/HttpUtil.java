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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import payten.bot.model.MenuItem;
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
    public void setCategories() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(stringUtils.getRestAdress() + getAllCategories, String.class);
        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        categories = objectMapper.readValue(responseBody, new TypeReference<ArrayList<String>>() {
        });
        logger.info(String.format(httpLogFormat, getAllCategories, responseEntity.getStatusCode(), responseEntity.getBody()));
    }
    public ArrayList<String> getCartList(String viberId) throws JsonProcessingException, URISyntaxException {
        ArrayList<String> cartList;
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + getCart + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        cartList = objectMapper.readValue(responseBody, new TypeReference<ArrayList<String>>() {
        });
        logger.info(String.format(httpLogFormat, getCart, responseEntity.getStatusCode(), responseEntity.getBody()));
        return cartList;
    }
    public ArrayList<String> getCurrentList(String viberId) throws JsonProcessingException, URISyntaxException {
        ArrayList<String> cartList;
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + convertToOrderModel + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        cartList = objectMapper.readValue(responseBody, new TypeReference<>() {
        });
        logger.info(String.format(httpLogFormat, convertToOrderModel, responseEntity.getStatusCode(), responseEntity.getBody()));
        return cartList;
    }
    public Boolean cartChecker(String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + checkIfUserCanOrder + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        Boolean status = Boolean.valueOf(responseEntity.getBody());
        logger.info(String.format(httpLogFormat, checkIfUserCanOrder, responseEntity.getStatusCode(), responseEntity.getBody()));
        return status;
    }
    public void removeServiceFromCart(String viberId, MenuItem itemName) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + removeItemFromCart + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MenuItem> requestEntity = new HttpEntity<>(itemName, headers);

        ResponseEntity<MenuItem> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, requestEntity, MenuItem.class);

        logger.info(String.format(httpLogFormat, removeItemFromCart, responseEntity.getStatusCode(), responseEntity.getBody()));
    }

    //Gathering items for selected category from menu
    public ArrayList<MenuItem> getServiceList(String menuItem) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(stringUtils.getRestAdress() + getCategoryItems + menuItem, String.class);
        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info(String.format(httpLogFormat, getCategoryItems, responseEntity.getStatusCode(), responseEntity.getBody()));
        return objectMapper.readValue(responseBody, new TypeReference<ArrayList<MenuItem>>() {
        });
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
    public Double getTotalPrice(String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + getTotalPrice + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        Double price = Double.valueOf(Objects.requireNonNull(responseEntity.getBody()));
        logger.info(String.format(httpLogFormat, getTotalPrice, responseEntity.getStatusCode(), responseEntity.getBody()));
        return price;

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
    public void clearCart(String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        URI uri = new URI(stringUtils.getRestAdress() + clearCart + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        HttpEntity<String> requestEntityPut = new HttpEntity<>("", headers);
        ResponseEntity<String> responseEntityPut = restTemplate.exchange(uri, HttpMethod.PUT, requestEntityPut, String.class);
        logger.info(String.format(httpLogFormat, clearCart, responseEntityPut.getStatusCode(), responseEntityPut.getBody()));
    }
        //Inserting selected service to DB in case User doesn't have field in DB here we create it.
        public boolean addServiceToCart(String viberId, MenuItem itemName) throws URISyntaxException, UnsupportedEncodingException {
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(stringUtils.getRestAdress() + addItemToCart + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<MenuItem> requestEntity = new HttpEntity<>(itemName, headers);
            try{
                ResponseEntity<MenuItem> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, MenuItem.class);
                return true;
            }
            catch (Exception e){
                return false;
            }

        }
    public void updateStartTime(String viberId,String startDate) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        URI uri = new URI(stringUtils.getRestAdress() + updateStartTime + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8) +"&startDate="+startDate);
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);        
        ResponseEntity<String> responseEntityPut = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, String.class);
        logger.info(String.format(httpLogFormat, clearCart, responseEntityPut.getStatusCode(), responseEntityPut.getBody()));
    }
    public MenuItem getItemByName(String itemName) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MenuItem> responseEntity = restTemplate.getForEntity(stringUtils.getRestAdress()+getItemByName.concat(itemName), MenuItem.class);
        logger.info(String.format(httpLogFormat, getItemByName, responseEntity.getStatusCode(), responseEntity.getBody()));
        MenuItem model = responseEntity.getBody();
        return model;
    }
    public ArrayList<OrderPOS> get24HOrderPOS () {
        RestTemplate restTemplate = new RestTemplate();
        String endpointUrl = stringUtils.getRestAdress() + getOrdersWithin24Hourse;      
        ParameterizedTypeReference<ArrayList<OrderPOS>> responseType = new ParameterizedTypeReference<ArrayList<OrderPOS>>() {};                
        ResponseEntity<ArrayList<OrderPOS>> responseEntity = restTemplate.exchange(endpointUrl, HttpMethod.GET, null, responseType);                
        ArrayList<OrderPOS> orderList = responseEntity.getBody();
        return orderList;
    }

}

