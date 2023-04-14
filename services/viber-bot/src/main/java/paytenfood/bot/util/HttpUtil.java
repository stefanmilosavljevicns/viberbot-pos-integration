package paytenfood.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import paytenfood.bot.model.MenuItem;
import paytenfood.bot.model.OrderAsseco;
import paytenfood.bot.model.OrderPOS;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import static paytenfood.bot.util.BotConstants.*;

@Component
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    @Autowired
    StringUtils stringUtils;
    private ArrayList<String> categories;

    public ArrayList<String> getCategories() {
        return categories;
    }

    //Creating start menu begins here, here we are gathering all categories from DB
    public void setCategories() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(stringUtils.getRestAdress() + getAllCategories, String.class);
        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        categories = objectMapper.readValue(responseBody, new TypeReference<ArrayList<String>>() {
        });
        logger.info(String.format(httpLogFormat, getAllCategories, responseEntity.getStatusCode(), responseEntity.getBody()));
    }

    //GENERATING CART LIST AND ADDING TOTAL PRICE AS LAST ITEM IN STRING LIST
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

    //Repacking current list into Strings so we can send it according to OrderPOS model, field: Description
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

    //CHECKING IF USER ADDED ANY ITEM TO CART IF THIS IS FALSE WE WILL NOT ALLOW USER TO GET CART PAGE OR FINISH ORDER
    public Boolean cartChecker(String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + checkIfCartIsEmpty + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        Boolean status = Boolean.valueOf(responseEntity.getBody());
        logger.info(String.format(httpLogFormat, checkIfCartIsEmpty, responseEntity.getStatusCode(), responseEntity.getBody()));
        return status;
    }

    //Inserting selected service to DB in case User doesn't have field in DB here we create it.
    public void addServiceToCart(String viberId, MenuItem itemName) throws URISyntaxException, UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("${rest.address}" + addItemToCart + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MenuItem> requestEntity = new HttpEntity<>(itemName, headers);

        ResponseEntity<MenuItem> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, MenuItem.class);

        logger.info(String.format(httpLogFormat, addItemToCart, responseEntity.getStatusCode(), responseEntity.getBody()));
    }
    //REMOVING ITEM FROM CART
    public void removeServiceFromCart(String viberId, MenuItem itemName) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI("${rest.address}" + removeItemFromCart + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MenuItem> requestEntity = new HttpEntity<>(itemName, headers);

        ResponseEntity<MenuItem> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, requestEntity, MenuItem.class);

        logger.info(String.format(httpLogFormat, removeItemFromCart, responseEntity.getStatusCode(), responseEntity.getBody()));
    }
    //We are using this for generating Asecco landing page
    public String generatePaymentId(String viberId, String merchantUser, String merchantPw, String merchant) throws URISyntaxException, JsonProcessingException {
        RestTemplate getRestTemplate = new RestTemplate();
        URI getURI = new URI(stringUtils.getRestAdress() + assecoOrderConverter + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = getRestTemplate.getForEntity(getURI, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info(String.format(httpLogFormat, assecoOrderConverter, responseEntity.getStatusCode(), responseEntity.getBody()));
        ArrayList<MenuItem> menuItemRead = objectMapper.readValue(responseEntity.getBody(), new TypeReference<ArrayList<MenuItem>>() {
        });
        ArrayList<OrderAsseco> orderAsseco = new ArrayList<>();
        for (MenuItem menuItem : menuItemRead) {
            OrderAsseco orderItem = new OrderAsseco();
            orderItem.setCode(menuItem.getName());
            orderItem.setName(menuItem.getName());
            orderItem.setDescription(menuItem.getDescription());
            orderItem.setQuantity(1);
            orderItem.setPrice(menuItem.getPrice());
            orderAsseco.add(orderItem);
        }
        String orderJson = null;
        ObjectMapper objectMapperOrder = new ObjectMapper();
        try {
            orderJson = objectMapperOrder.writeValueAsString(orderAsseco);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Double totalPrice = getTotalPrice(viberId);
        //Generating Form body for obtaining token
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("ACTION", "SESSIONTOKEN");
        map.add("MERCHANTUSER", merchantUser);
        map.add("MERCHANTPASSWORD", merchantPw);
        map.add("MERCHANT", merchant);
        map.add("CUSTOMER", viberId);
        map.add("SESSIONTYPE", "PAYMENTSESSION");
        map.add("MERCHANTPAYMENTID", LocalDateTime.now() + "_" + viberId);
        map.add("AMOUNT", totalPrice);
        map.add("CURRENCY", "RSD");
        map.add("RETURNURL", redirection + "?viberId=" + viberId + "&viberPath=" + botPath);
        map.add("ORDER", orderJson);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(assecoPayingOnline, HttpMethod.POST, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper objectMapperAsecco = new ObjectMapper();
            JsonNode rootNode = objectMapperAsecco.readTree(response.getBody());
            logger.info(String.format(httpLogFormat, assecoPayingOnline, response.getStatusCode(), response.getBody()));
            return rootNode.get("sessionToken").asText();
        } else {
            return null;
        }
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

    public Boolean getIsPayingStatus(String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + checkPayingStatus + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        logger.info(String.format(httpLogFormat, checkPayingStatus, responseEntity.getStatusCode(), responseEntity.getBody()));
        Boolean status = Boolean.valueOf(responseEntity.getBody());
        return status;
    }

    public Double getTotalTime(String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + getTotalTime + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        Double time = Double.valueOf(Objects.requireNonNull(responseEntity.getBody()));
        logger.info(String.format(httpLogFormat, getTotalTime, responseEntity.getStatusCode(), responseEntity.getBody()));
        return time;

    }

    public Double getTotalPrice(String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + getTotalPrice + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        Double price = Double.valueOf(Objects.requireNonNull(responseEntity.getBody()));
        logger.info(String.format(httpLogFormat, getTotalPrice, responseEntity.getStatusCode(), responseEntity.getBody()));
        return price;

    }

    public void changeIsPayingStatus(String viberId, Boolean payingStatus) throws URISyntaxException, UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(stringUtils.getRestAdress() + changePayingStatus + "?payingStatus=" + URLEncoder.encode(String.valueOf(payingStatus), StandardCharsets.UTF_8) + "&viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, String.class);
        logger.info(String.format(httpLogFormat, changePayingStatus, responseEntity.getStatusCode(), responseEntity.getBody()));
    }

    //In this endpoint we are sending order to POS and clearing cart for customer
    public void sendOrder(OrderPOS orderPOS, String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderPOS> requestEntity = new HttpEntity<>(orderPOS, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(stringUtils.getRestAdress() + addOrder, HttpMethod.POST, requestEntity, String.class);
        logger.info(String.format(httpLogFormat, addOrder, responseEntity.getStatusCode(), responseEntity.getBody()));
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            restTemplate = new RestTemplate();
            URI uri = new URI(stringUtils.getRestAdress() + clearCart + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
            HttpEntity<String> requestEntityPut = new HttpEntity<>("", headers);
            ResponseEntity<String> responseEntityPut = restTemplate.exchange(uri, HttpMethod.PUT, requestEntityPut, String.class);
            logger.info(String.format(httpLogFormat, clearCart, responseEntityPut.getStatusCode(), responseEntityPut.getBody()));
        }
    }

    public MenuItem getItemByName(String itemName) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MenuItem> responseEntity = restTemplate.getForEntity(getItemByName.concat(itemName), MenuItem.class);
        logger.info("Calling endpoint: " + getItemByName);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
        logger.info(String.format(httpLogFormat, getItemByName, responseEntity.getStatusCode(), responseEntity.getBody()));
        MenuItem model = responseEntity.getBody();
        return model;
    }


}

