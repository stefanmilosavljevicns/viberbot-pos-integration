package paytenfood.bot.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paytenfood.bot.model.Order;
import paytenfood.bot.util.DateUtil;
import paytenfood.bot.util.HttpUtil;
import paytenfood.bot.util.KeyboardUtil;
import ru.multicon.viber4j.ViberBot;
import ru.multicon.viber4j.ViberBotManager;
import ru.multicon.viber4j.account.UserDetails;
import ru.multicon.viber4j.incoming.Incoming;
import ru.multicon.viber4j.incoming.IncomingImpl;
import ru.multicon.viber4j.keyboard.ViberKeyboard;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static paytenfood.bot.util.StringUtils.*;

@RestController
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private final String MESSAGE_EVENT = "message";
    private final String START_MSG_EVENT = "conversation_started";
    @Value("${viber.token}")
    private String botToken;
    @Value("${viber.web-hook}")
    private String webHookUrl;
    @Value("${viber.media-source-url}")
    private String mediaSourceUrl;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private KeyboardUtil keyboardUtil;

    @RequestMapping(method = POST, path = "/viberbot")
    ResponseEntity<?> callbackHandle(@RequestBody String text) throws IOException, InterruptedException, URISyntaxException {
        logger.info("Received messageForUser {}", text);
        // Processing incoming messageForUser
        Incoming incoming = IncomingImpl.fromString(text);
        String eventType = incoming.getEvent();
        logger.info("Event type {}", eventType);
        if (!StringUtils.equals(eventType, MESSAGE_EVENT) && !StringUtils.equals(incoming.getEvent(), START_MSG_EVENT))
            return ResponseEntity.ok().build();

        String userName = incoming.getSenderName();
        String userId = incoming.getSenderId();
        String messageText = incoming.getMessageText();
        logger.info("Message text: {}", messageText);
        //
        // Viber bot instance
        //
        ViberBot bot = ViberBotManager.viberBot(botToken);

        //
        // getting info about user
        //
        UserDetails userDet = bot.getUserDetails(userId);
        logger.info("User country: {}", userDet.getCountry());
        logger.info("User device: {}", userDet.getDeviceType());

        //FIRST WE NEED TO CHECK IF USER IS AT LAST STAGE OF RESERVATION
        if (httpUtil.getIsPayingStatus(userId)) {

            LocalDateTime startTime = dateUtil.parseUserInput(messageText);
            ViberKeyboard keyboard = createStartKeyboard();
            //CHECKING IF USER INPUT IS IN CORRECT FORM DAY.MONTH/HOUR:MIN
            if (startTime == null) {
                bot.messageForUser(userId).postText(ERROR_TIME, keyboard);
                httpUtil.changeIsPayingStatus(userId, false);
            }
            else{
                //IF FORM IS CORRECT CHECKING IF TIMESLOT IS AVAILABLE
                Double totalMinutes = httpUtil.getTotalTime(userId);
                LocalDateTime endTime = dateUtil.setEndDate(startTime,totalMinutes);
                String checkTime = httpUtil.checkIfTimeIsAvailable(startTime,endTime);
                if(checkTime.equals("Time slot is available.")){
                    Order sendOrder = new Order(httpUtil.getCurrentList(userId),httpUtil.getTotalPrice(userId),startTime,endTime,"PENDING",userId);
                    httpUtil.sendOrder(sendOrder,userId);
                    bot.messageForUser(userId).postText("Uspešno ste završili rezervaciju!",keyboard);
                    httpUtil.changeIsPayingStatus(userId, false);
                }
                else{
                    bot.messageForUser(userId).postText(checkTime);
                }
            }

        } else if (StringUtils.equals("LST", messageText.substring(0, 3))) {
            logger.info(String.format("Showing category list for %s", messageText.substring(3)));
            ViberKeyboard keyboard = keyboardUtil.setListMenu(messageText.substring(3));
            bot.messageForUser(userId).postText("Prikazujem listu " + messageText.substring(3), keyboard);
        } else if (StringUtils.equals("ADD", messageText.substring(0, 3))) {
            logger.info("Adding to cart: " + messageText.substring(3));
            httpUtil.addServiceToCart(userId, messageText.substring(3));
            ViberKeyboard keyboard = createStartKeyboard();
            bot.messageForUser(userId).postText(messageText.substring(3) + " je uspešno dodat na listu.", keyboard);
        } else if (StringUtils.equals("CART", messageText)) {
            ViberKeyboard keyboard;
            if (httpUtil.cartChecker(userId)) {
                keyboard = keyboardUtil.setCartList(userId);
                bot.messageForUser(userId).postText("Prikazujem izabrane usluge.", keyboard);
                logger.info("Showing current cart.");
            } else {
                keyboard = createStartKeyboard();
                bot.messageForUser(userId).postText(ERROR_CART, keyboard);
                logger.info("Unable to show current cart.");
            }
        } else if (StringUtils.equals("FINISH", messageText)) {
            ViberKeyboard keyboard;
            //TODO Prebaci ovo u StringUtil kada ga napravis
            if (httpUtil.cartChecker(userId)) {
                String finishMsg = CHECK_CART;
                for (String element : httpUtil.getCartList(userId)) {
                    finishMsg += element + "\n";
                }
                keyboard = keyboardUtil.setYesNo();
                bot.messageForUser(userId).postText(finishMsg, keyboard);
                logger.info("User have enough cart items to proceed with reservation.");
            } else {
                keyboard = createStartKeyboard();
                bot.messageForUser(userId).postText(ERROR_CART, keyboard);
                logger.info("Unable to show current cart.");
            }
        } else if (StringUtils.equals("PAYMENT", messageText)) {
            ViberKeyboard keyboard;
            //TODO Prebaci ovo u StringUtil kada ga napravis
            //Sending user to double check his CART before proceeding to chose time and payment method
            if (httpUtil.cartChecker(userId)) {
                keyboard = keyboardUtil.setPaymentOption();
                bot.messageForUser(userId).postText(CHECK_PAYMENT, keyboard);
                logger.info("Asking user if he agrees with his cart.");
            } else {
                keyboard = createStartKeyboard();
                bot.messageForUser(userId).postText(ERROR_CART, keyboard);
                logger.info("Unable to show current cart.");
            }
        } else if (StringUtils.equals("TIME", messageText)) {
            bot.messageForUser(userId).postText(CHECK_TIME);
            httpUtil.changeIsPayingStatus(userId, true);
        } else if (StringUtils.equals("RMV", messageText.substring(0, 3))) {
            logger.info("Trying to remove: " + messageText.substring(3));
            httpUtil.removeCartItem(userId, messageText.substring(3));
            ViberKeyboard keyboard = keyboardUtil.setCartList(userId);
            bot.messageForUser(userId).postText(messageText.substring(3) + " je uspešno uklonjena.", keyboard);
        }else if (StringUtils.equals("RETURNTOSTART",messageText)){
            ViberKeyboard keyboard = createStartKeyboard();
            bot.messageForUser(userId).postText(RETURN_MENU, keyboard);
        }
        else {
            if (!StringUtils.equals("IGNORE", messageText)) {
                ViberKeyboard keyboard = createStartKeyboard();
                bot.messageForUser(userId).postText(WELCOME_MESSAGE, keyboard);
            }
        }

        return ResponseEntity.ok().build();
    }


    private ViberKeyboard createStartKeyboard() {
        return keyboardUtil.getMainMenu();
    }

}
        

