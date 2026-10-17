package payten.bot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = BotApplication.class, properties = {
		"bot.initialize-menu-on-startup=false",
		"reminders.enabled=false"
})
class BotApplicationTests {

	@Test
	void contextLoads() {
	}

}
