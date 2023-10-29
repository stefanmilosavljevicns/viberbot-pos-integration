package payten.bot.scheduler;

import com.payten.restapi.model.Order;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import org.slf4j.Logger;
import payten.bot.model.OrderPOS;
import payten.bot.util.HttpUtil;
import payten.bot.util.KeyboardUtil;
import com.payten.viberutil.ViberBot;
import com.payten.viberutil.ViberBotManager;

@Component
public class OrderReminderScheduler {
    private static final Logger logger = LoggerFactory.getLogger(OrderReminderScheduler.class);

    @Autowired
    public payten.bot.util.StringUtils stringUtils;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private KeyboardUtil keyboardUtil;
    @Scheduled(cron = "0 0 14 * * *") // Trigger at 2 pm every day
    public void remindUserForIncomingReservation() {        
        ViberBot bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        ArrayList<Order> activeUsers = new ArrayList<>();
        activeUsers = httpUtil.get24HOrderPOS();
        if(activeUsers.size() > 0){
            for(Order orderPos : activeUsers){
                bot.messageForUser(orderPos.getViberID()).postText(stringUtils.getMessageReminder() + orderPos.getStartTime().getHour() +":" + orderPos.getStartTime().getMinute(),keyboardUtil.getMainMenu());
                logger.info("Reminding user " + orderPos.getViberID() + " for his reservation");
            }
        }
    }
}
