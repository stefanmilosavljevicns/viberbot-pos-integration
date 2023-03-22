package paytenfood.bot.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paytenfood.bot.util.HttpUtil;
import paytenfood.bot.util.KeyboardUtil;
import ru.multicon.viber4j.ViberBot;
import ru.multicon.viber4j.ViberBotManager;
import ru.multicon.viber4j.account.UserDetails;
import ru.multicon.viber4j.incoming.Incoming;
import ru.multicon.viber4j.incoming.IncomingImpl;
import ru.multicon.viber4j.keyboard.ViberButton;
import ru.multicon.viber4j.keyboard.ViberKeyboard;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static paytenfood.bot.util.StringUtils.*;

@RestController
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private final String MESSAGE_EVENT = "message";
    private final String START_MSG_EVENT = "conversation_started";
    boolean placam = false;
    @Value("${viber.token}")
    private String botToken;
    @Value("${viber.web-hook}")
    private String webHookUrl;
    @Value("${viber.media-source-url}")
    private String mediaSourceUrl;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private KeyboardUtil keyboardUtil;
    String finishMsg;
    private final ArrayList<String> orderedItems = new ArrayList<>();
    private int price = 0;

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


         if (StringUtils.equals("0", messageText)) {
            ViberKeyboard keyboard = createStartKeyboard();
            bot.messageForUser(userId).postText(RETURN_MENU, keyboard);
        } else if (StringUtils.equals("LIST", messageText.substring(0, 4))) {
            ViberKeyboard keyboard = keyboardUtil.setListMenu(messageText.substring(4));
            logger.info(String.format("Showing listMenu for %s", messageText.substring(4)));
            bot.messageForUser(userId).postText("Prikazujem listu " + messageText.substring(4), keyboard);
        } else if (StringUtils.equals("ADD", messageText.substring(0, 3))) {
            logger.info("Trying to add to menu: " + messageText.substring(3));
            httpUtil.addServiceToCart(userId, messageText.substring(3));
            ViberKeyboard keyboard = createStartKeyboard();
            logger.info(String.format("Showing listMenu for %s", messageText.substring(3)));
            bot.messageForUser(userId).postText(messageText.substring(3) + " je uspešno dodat na listu.", keyboard);
        } else if (StringUtils.equals("CART", messageText)) {
            ViberKeyboard keyboard;
            if (httpUtil.statusChecker(userId)) {
                keyboard = keyboardUtil.setCartList(userId);
                bot.messageForUser(userId).postText("Prikazujem izabrane usluge.", keyboard);
            } else {
                keyboard = createStartKeyboard();
                bot.messageForUser(userId).postText(ERROR_CART, keyboard);
            }
        } else if (StringUtils.equals("FINISH", messageText)) {
            ViberKeyboard keyboard;
            //TODO Prebaci ovo u StringUtil kada ga napravis
            if (httpUtil.statusChecker(userId)) {
                finishMsg = CHECK_CART;
                for (String element : httpUtil.getCartList(userId)) {
                    finishMsg+= element + "\n";
                }
                keyboard = keyboardUtil.setYesNo();
                bot.messageForUser(userId).postText(finishMsg, keyboard);
            } else {
                keyboard = createStartKeyboard();
                bot.messageForUser(userId).postText(ERROR_CART, keyboard);
            }
        } else if (StringUtils.equals("PAYMENT", messageText)) {
             ViberKeyboard keyboard;
             logger.info("USAO SAM");
             //TODO Prebaci ovo u StringUtil kada ga napravis
             if (httpUtil.statusChecker(userId)) {
                 logger.info("USAO SAM I OVDE");
                 keyboard = keyboardUtil.setPaymentOption();
                 logger.info("USAO SAM I OVDE OVDE");
                 bot.messageForUser(userId).postText(CHECK_PAYMENT, keyboard);
             } else {
                 keyboard = createStartKeyboard();
                 bot.messageForUser(userId).postText(ERROR_CART, keyboard);
             }
         } else if (StringUtils.equals("TIME", messageText)) {
             bot.messageForUser(userId).postText(CHECK_TIME);
         } else if (StringUtils.equals("RMV", messageText.substring(0, 3))) {
            logger.info("Trying to remove: " + messageText.substring(3));
            httpUtil.removeCartItem(userId, messageText.substring(3));
            ViberKeyboard keyboard = keyboardUtil.setCartList(userId);;
            bot.messageForUser(userId).postText(messageText.substring(3)+" je uspešno uklonjena.", keyboard);
        } else {
             if(!StringUtils.equals("IGNORE",messageText)){
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
        

