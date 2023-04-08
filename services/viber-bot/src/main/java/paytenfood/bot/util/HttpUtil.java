package paytenfood.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static paytenfood.bot.util.StringUtils.*;

@Component
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private ArrayList<String> categories;

    public ArrayList<String> getCategories() {
        return categories;
    }

    //Creating start menu begins here, here we are gathering all categories from DB
    public void setCategories() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(urlMenu, String.class);
        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        categories = objectMapper.readValue(responseBody, new TypeReference<ArrayList<String>>() {
        });
        logger.info("Calling endpoint: " + rmvItems);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
    }
    //GENERATING CART LIST AND ADDING TOTAL PRICE AS LAST ITEM IN STRING LIST
    public ArrayList<String> getCartList(String viberId) throws JsonProcessingException {
        ArrayList<String> cartList;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(getCart.concat(viberId), String.class);
        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        cartList = objectMapper.readValue(responseBody, new TypeReference<ArrayList<String>>() {
        });
        logger.info("Calling endpoint: " + getCart);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
        return cartList;
    }
    //Repacking current list into Strings so we can send it according to OrderPOS model, field: Description
    public ArrayList<String> getCurrentList(String viberId) throws JsonProcessingException {
        ArrayList<String> cartList;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(getCurrentList.concat(viberId), String.class);
        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        cartList = objectMapper.readValue(responseBody, new TypeReference<>() {
        });
        logger.info("Calling endpoint: " + getCurrentList);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
        return cartList;
    }
    //CHECKING IF USER ADDED ANY ITEM TO CART IF THIS IS FALSE WE WILL NOT ALLOW USER TO GET CART PAGE OR FINISH ORDER
    public Boolean cartChecker(String viberId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(checkCart.concat(viberId), String.class);
        Boolean status = Boolean.valueOf(responseEntity.getBody());
        logger.info("Calling endpoint: " + checkCart);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
        if(status){
            return true;
        }
        return false;
    }
    //Inserting selected service to DB in case User doesn't have field in DB here we create it.
    public void addServiceToCart(String viberId, MenuItem itemName) throws URISyntaxException, UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(addItems+"?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MenuItem> requestEntity = new HttpEntity<>(itemName, headers);

        ResponseEntity<MenuItem> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, MenuItem.class);

        logger.info("Calling endpoint: " + addItems);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
    }
    //We are using this for generating Asecco landing page
    public String generatePaymentId(String viberId, String merchantUser, String merchantPw, String merchant) throws URISyntaxException, JsonProcessingException {
        RestTemplate getRestTemplate = new RestTemplate();
        URI getURI = new URI(assecoGetCurrentCart+"?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = getRestTemplate.getForEntity(getURI,String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<MenuItem> menuItemRead = objectMapper.readValue(responseEntity.getBody(), new TypeReference<ArrayList<MenuItem>>() {});
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
        MultiValueMap<String, Object> map= new LinkedMultiValueMap<>();
        map.add("ACTION","SESSIONTOKEN");
        map.add("MERCHANTUSER",merchantUser);
        map.add("MERCHANTPASSWORD",merchantPw);
        map.add("MERCHANT",merchant);
        map.add("CUSTOMER",viberId);
        map.add("SESSIONTYPE","PAYMENTSESSION");
        map.add("MERCHANTPAYMENTID", LocalDateTime.now() +"_"+viberId);
        map.add("AMOUNT",totalPrice);
        map.add("CURRENCY","RSD");
        map.add("RETURNURL",redirection+"?viberID="+viberId+"&viberPath="+botPath);
        map.add("ORDER",orderJson);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(assecoPayingOnline, HttpMethod.POST, request, String.class);
        if(response.getStatusCode() == HttpStatus.OK){
            ObjectMapper objectMapperAsecco = new ObjectMapper();
            JsonNode rootNode = objectMapperAsecco.readTree(response.getBody());
            logger.info("Calling endpoint: " + assecoPayingOnline);
            logger.info("Response status: " + response.getStatusCode());
            logger.info("Response body: " + responseEntity.getBody());
            return rootNode.get("sessionToken").asText();
        }
        else{
            return null;
        }
    }
    //REMOVING ITEM FROM CART
    public void removeCartItem(String viberId, MenuItem itemName) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(rmvItems+"?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MenuItem> requestEntity = new HttpEntity<>(itemName, headers);

        ResponseEntity<MenuItem> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, requestEntity, MenuItem.class);

        logger.info("Calling endpoint: " + rmvItems);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
    }
    //Gathering items for selected category from menu
    public ArrayList<MenuItem> getServiceList(String menuItem) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(urlItems.concat(menuItem), String.class);
        String responseBody = responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("Calling endpoint: " + urlItems);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
        return objectMapper.readValue(responseBody, new TypeReference<ArrayList<MenuItem>>() {
        });
    }

    public String checkIfTimeIsAvailable(LocalDateTime startDate,LocalDateTime endDate) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(checkIfTimeIsAvailable+ "?start=" + URLEncoder.encode(String.valueOf(startDate), StandardCharsets.UTF_8)
                + "&end=" + URLEncoder.encode(String.valueOf(endDate), StandardCharsets.UTF_8));
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri,String.class);
        logger.info("Calling endpoint: " + checkIfTimeIsAvailable);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
        return responseEntity.getBody();
    }
    public Boolean getIsPayingStatus(String viberId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(checkPayingStatus.concat(viberId), String.class);
        logger.info("Calling endpoint: " + checkPayingStatus);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
        Boolean status = Boolean.valueOf(responseEntity.getBody());
        return status;
    }
    public Double getTotalTime(String viberId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(findTotalTime.concat(viberId), String.class);
        Double time = Double.valueOf(Objects.requireNonNull(responseEntity.getBody()));
        logger.info("Calling endpoint: " + findTotalTime);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
        return time;

    }

    public Double getTotalPrice(String viberId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(findTotalPrice.concat(viberId), String.class);
        Double price = Double.valueOf(Objects.requireNonNull(responseEntity.getBody()));
        logger.info("Calling endpoint: " + findTotalPrice);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
        return price;

    }
    public void changeIsPayingStatus(String viberId, Boolean payingStatus) throws URISyntaxException, UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(changePayingStatus + "?payingStatus=" + URLEncoder.encode(String.valueOf(payingStatus), StandardCharsets.UTF_8)
                + "&viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, String.class);

        logger.info("Calling endpoint: " + changePayingStatus);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
    }
    public void sendOrder(OrderPOS orderPOS, String viberId) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderPOS> requestEntity = new HttpEntity<>(orderPOS, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(sendOrder, HttpMethod.POST, requestEntity, String.class);
        if(responseEntity.getStatusCode().equals(HttpStatus.OK)){
            restTemplate = new RestTemplate();
            URI uri = new URI(completeOrder + "?viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));
            HttpEntity<String> requestEntityPut = new HttpEntity<>("", headers);
            ResponseEntity<String> responseEntityPut = restTemplate.exchange(uri, HttpMethod.PUT, requestEntityPut, String.class);
            logger.info("Calling endpoint: " + completeOrder);
            logger.info("Response status: " + responseEntityPut.getStatusCode());
            logger.info("Response body: " + responseEntityPut.getBody());
        }
    }
    public MenuItem getItemByName(String itemName){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MenuItem> responseEntity = restTemplate.getForEntity(findItem.concat(itemName), MenuItem.class);
        logger.info("Calling endpoint: " + findItem);
        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
        MenuItem model = responseEntity.getBody();
        return model;
    }



}

