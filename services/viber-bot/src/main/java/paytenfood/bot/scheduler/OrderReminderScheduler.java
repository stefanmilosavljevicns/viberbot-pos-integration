package paytenfood.bot.scheduler;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import org.slf4j.Logger;
import paytenfood.bot.model.OrderPOS;
import paytenfood.bot.util.HttpUtil;
import paytenfood.bot.util.KeyboardUtil;
import com.payten.viberutil.ViberBot;
import com.payten.viberutil.ViberBotManager;

@Component
public class OrderReminderScheduler {
    private static final Logger logger = LoggerFactory.getLogger(OrderReminderScheduler.class);

    @Autowired
    public paytenfood.bot.util.StringUtils stringUtils;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private KeyboardUtil keyboardUtil;
    @Scheduled(cron = "0 0 14 * * *") // Trigger at 2 pm every day
    public void remindUserForIncomingReservation() {        
        ViberBot bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        ArrayList<OrderPOS> activeUsers = new ArrayList<>();
        activeUsers = httpUtil.get24HOrderPOS();
        if(activeUsers.size() > 0){
            for(OrderPOS orderPos : activeUsers){
                bot.messageForUser(orderPos.getViberID()).postText("Podsećamo Vas da sutra imate zakazan termin u " + orderPos.getStartTime().getHour() +":" + orderPos.getStartTime().getMinute(),keyboardUtil.getMainMenu());
                logger.info("Reminding user " + orderPos.getViberID() + " for his reservation");
            }
        }
    }
}
