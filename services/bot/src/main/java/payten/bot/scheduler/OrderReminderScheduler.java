package payten.bot.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.ArrayList;
import org.slf4j.Logger;
import payten.bot.model.OrderPOS;
import payten.bot.util.HttpUtil;
import payten.bot.util.KeyboardUtil;
import com.payten.viberutil.ViberBot;
import com.payten.viberutil.ViberBotManager;
import payten.bot.util.LocaleUtil;

import static payten.bot.util.BotConstants.controlerLogFormat;

@Component
public class OrderReminderScheduler {
    private static final Logger logger = LoggerFactory.getLogger(OrderReminderScheduler.class);
    @Autowired
    public payten.bot.util.StringUtils stringUtils;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private KeyboardUtil keyboardUtil;
    @Autowired
    private LocaleUtil localeUtil;
    @Scheduled(cron = "0 0 14 * * *") // Trigger at 2 pm every day
    public void remindUserForIncomingReservation() throws URISyntaxException, JsonProcessingException {
        ViberBot bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        ArrayList<OrderPOS> activeUsers = new ArrayList<>();
        activeUsers = httpUtil.get24HOrderPOS();
        if(activeUsers.size() > 0){
            for(OrderPOS orderPos : activeUsers){
                String userLocale = httpUtil.getUserLocale(orderPos.getViberID());
                bot.messageForUser(orderPos.getViberID()).postText(localeUtil.getLocalizedMessage("message.reminder-reservation",userLocale) + orderPos.getStartTime().getHour() +":" + orderPos.getStartTime().getMinute(),keyboardUtil.getMainMenu(userLocale));
                logger.info(String.format(controlerLogFormat,"Reminding user for his reservation",orderPos.getViberID()));
            }
        }
    }
}
