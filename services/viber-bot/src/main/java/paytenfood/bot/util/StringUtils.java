package paytenfood.bot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StringUtils {
    @Value("${viber.token}")
    private String botToken;
    @Value("${viber.web-hook}")
    private String webHookUrl;
    @Value("${viber.bot-path}")
    private String botPath;
    @Value("${rest.address}")
    private String restAdress;
    @Value("${color.primarily}")
    private String primarilyColor;
    @Value("${color.secondarily}")
    private String secondarilyColor;

    public String getBotToken() {
        return botToken;
    }



    public String getWebHookUrl() {
        return webHookUrl;
    }



    public String getRestAdress() {
        return restAdress;
    }




}
