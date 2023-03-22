package paytenfood.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import paytenfood.bot.model.ListModel;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

@Component
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private final String urlMenu = "http://rest:9097/api/v1/getallCategories";
    private final String urlItems = "http://rest:9097/api/v1/getCategoryItems/";
    private final String addItems = "http://rest:9097/api/v1/addListItem";
    private final String rmvItems = "http://rest:9097/api/v1/removeListItem";
    private final String findPrice = "http://rest:9097/api/v1/getPriceByName/";
    private final String getCart = "http://rest:9097/api/v1/getListByViberId?viberId=";
    private final String checkCart = "http://rest:9097/api/v1/getActiveOrders?viberId=";
    private ArrayList<String> categories;

    public ArrayList<String> getCategories() {
        return categories;
    }

    //Creating start menu begins here, here we are gathering all categories from DB
    public void setCategories() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(urlMenu, String.class);
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        categories = objectMapper.readValue(responseBody, new TypeReference<ArrayList<String>>() {
        });
        logger.info("Categories have been loaded successfully!");
    }

    public ArrayList<String> getCartList(String viberId) throws JsonProcessingException {
        ArrayList<String> cartList;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(getCart.concat(viberId), String.class);
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        cartList = objectMapper.readValue(responseBody, new TypeReference<ArrayList<String>>() {
        });
        return cartList;
    }

    public Boolean statusChecker(String viberId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(checkCart.concat(viberId), String.class);
        Boolean status = Boolean.valueOf(response.getBody());
        if(status){
            return true;
        }
        return false;

    }
    public void removeCartItem(String viberId,String itemName) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(rmvItems + "?newItem=" + URLEncoder.encode(itemName, StandardCharsets.UTF_8)
                + "&price=" + URLEncoder.encode(Double.toString(getPriceOfItem(itemName)), StandardCharsets.UTF_8)
                + "&viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, requestEntity, String.class);

        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
    }
    //Gathering items for selected category from menu
    public ArrayList<ListModel> getServiceList(String menuItem) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(urlItems.concat(menuItem), String.class);
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<ListModel> listModels = objectMapper.readValue(responseBody, new TypeReference<ArrayList<ListModel>>() {
        });
        logger.info(String.format("Successfully obtained menuItems from  %s", menuItem));
        return listModels;
    }

    public double getPriceOfItem(String itemName){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(findPrice.concat(itemName), String.class);
        logger.info("Obtaining price... Response status: " + response.getStatusCode());
        return Double.parseDouble(Objects.requireNonNull(response.getBody()));
    }

    //Inserting selected service to DB in case User doesn't have field in DB here we create it.
    public void addServiceToCart(String viberId, String itemName) throws URISyntaxException, UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(addItems + "?newItem=" + URLEncoder.encode(itemName, StandardCharsets.UTF_8)
                + "&price=" + URLEncoder.encode(Double.toString(getPriceOfItem(itemName)), StandardCharsets.UTF_8)
                + "&viberId=" + URLEncoder.encode(viberId, StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, String.class);

        logger.info("Response status: " + responseEntity.getStatusCode());
        logger.info("Response body: " + responseEntity.getBody());
    }

}

