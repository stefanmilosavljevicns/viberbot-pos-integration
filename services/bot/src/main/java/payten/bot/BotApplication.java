package payten.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import payten.bot.util.HttpUtil;
import payten.bot.util.KeyboardUtil;
import payten.bot.util.StringUtils;
import com.payten.viberutil.ViberBotManager;



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
	@Value("${viber.webhook.register-on-startup:false}")
	private boolean registerWebhookOnStartup;
	@Value("${viber.webhook.base-url:}")
	private String webhookBaseUrl;
	@Value("${bot.initialize-menu-on-startup:true}")
	private boolean initializeMenuOnStartup;
	public static void main(String[] args) {
		SpringApplication.run(BotApplication.class, args);
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (initializeMenuOnStartup) {
			try {
				httpUtil.setCategories();
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Unable to load menu categories from the REST API.", e);
			}
			keyboardUtil.setMainMenu();
		}
		if (registerWebhookOnStartup) {
			if (stringUtils.getBotToken().isBlank() || webhookBaseUrl.isBlank()) {
				throw new IllegalStateException("Webhook registration requires VIBER_BOT_TOKEN and VIBER_WEBHOOK_BASE_URL.");
			}
			String callbackUrl = webhookBaseUrl.replaceAll("/$", "") + stringUtils.getBotPath();
			logger.info("Registering Viber webhook at configured callback URL.");
			if (!ViberBotManager.viberBot(stringUtils.getBotToken()).setWebHook(callbackUrl))
				logger.error("Viber webhook registration failed.");
		}


    }

}
