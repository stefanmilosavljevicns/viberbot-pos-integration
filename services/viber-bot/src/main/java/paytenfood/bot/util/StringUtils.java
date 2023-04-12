package paytenfood.bot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StringUtils {
    @Value("${viber.token}")
    private String botToken;
    @Value("${viber.web-hook}")
    private String webHookUrl;

    private String primarilyColor;
}
