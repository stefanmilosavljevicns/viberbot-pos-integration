package paytenfood.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import paytenfood.bot.model.ListModel;
import java.util.ArrayList;

@Component
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private ArrayList<String> categories;
    private String urlMenu = "http://rest:9097/api/v1/getallCategories";
    private String urlItems = "http://rest:9097/api/v1/getCategoryItems/";

    public ArrayList<String> getCategories(){
        return categories;
    }
    public void setCategories() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(urlMenu, String.class);
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        categories = objectMapper.readValue(responseBody, new TypeReference<ArrayList<String>>(){});
        logger.info("Categories have been loaded successfully!");
    }
    public ArrayList<ListModel> getList(String menuItem) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(urlItems.concat(menuItem), String.class);
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<ListModel> listModels = objectMapper.readValue(responseBody, new TypeReference<ArrayList<ListModel>>(){});
        logger.info(String.format("Successfully obtained menuItems from  %s",menuItem));
        return listModels;
    }

}

