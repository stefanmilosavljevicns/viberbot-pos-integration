package paytenfood.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
//import com.payten.FoodRest.Model;
import java.util.ArrayList;

@Component
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private ArrayList<String> categories;
    private String url = "http://rest:9097/api/v1/getallCategories";

    public ArrayList<String> getCategories(){
        return categories;
    }
    public void setCategories() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        categories = objectMapper.readValue(responseBody, new TypeReference<ArrayList<String>>(){});
        logger.info("Categories have been loaded successfully!");
    }

}

