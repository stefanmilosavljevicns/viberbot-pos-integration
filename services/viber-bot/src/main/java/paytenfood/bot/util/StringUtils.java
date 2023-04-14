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
    private String primarilyColor;

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getWebHookUrl() {
        return webHookUrl;
    }

    public void setWebHookUrl(String webHookUrl) {
        this.webHookUrl = webHookUrl;
    }

    public String getRestAdress() {
        return restAdress;
    }

    public void setRestAdress(String restAdress) {
        this.restAdress = restAdress;
    }

    public String getPrimarilyColor() {
        return primarilyColor;
    }

    public void setPrimarilyColor(String primarilyColor) {
        this.primarilyColor = primarilyColor;
    }
}
