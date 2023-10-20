package paytenfood.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import paytenfood.bot.util.HttpUtil;
import paytenfood.bot.util.KeyboardUtil;
import paytenfood.bot.util.StringUtils;
import com.payten.viberutil.ViberBotManager;

import static paytenfood.bot.util.BotConstants.domain;


@SpringBootApplication
@EnableScheduling
public class BotApplication  implements ApplicationListener<ApplicationReadyEvent> {
	private static final Logger logger = LoggerFactory.getLogger(BotApplication.class);
    @Autowired
    private StringUtils stringUtils;
    @Autowired
    KeyboardUtil keyboardUtil;
    @Autowired
    private HttpUtil httpUtil;
	public static void main(String[] args) {
		SpringApplication.run(BotApplication.class, args);
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            httpUtil.setCategories();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        keyboardUtil.setMainMenu();
		logger.info("Web-hook registration for {}", domain+stringUtils.getBotPath());
        if (!ViberBotManager.viberBot(stringUtils.getBotToken()).setWebHook(domain+stringUtils.getBotPath()))
            logger.error("Web-hook registration failed!");


    }

}
