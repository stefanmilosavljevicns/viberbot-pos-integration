package paytenfood.bot.controller;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import paytenfood.bot.model.MenuItem;
import paytenfood.bot.model.OrderPOS;
import paytenfood.bot.util.DateUtil;
import paytenfood.bot.util.HttpUtil;
import paytenfood.bot.util.KeyboardUtil;
import ru.multicon.viber4j.ViberBot;
import ru.multicon.viber4j.ViberBotManager;
import ru.multicon.viber4j.account.UserDetails;
import ru.multicon.viber4j.incoming.Incoming;
import ru.multicon.viber4j.incoming.IncomingImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Map;

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
        ViberBot bot = ViberBotManager.viberBot(botToken);
        Incoming incoming = IncomingImpl.fromString(text);
        String eventType = incoming.getEvent();
        logger.info("Event type {}", eventType);
        //
        // getting info about user
        //
        String userName = incoming.getSenderName();
        String userId = incoming.getSenderId();
        String messageText = incoming.getMessageText();
        logger.info("Message text: {}", messageText);

        if (!StringUtils.equals(eventType, MESSAGE_EVENT) && !StringUtils.equals(incoming.getEvent(), START_MSG_EVENT))
            return ResponseEntity.ok().build();
        UserDetails userDet = bot.getUserDetails(userId);
        logger.info("User country: {}", userDet.getCountry());
        logger.info("User device: {}", userDet.getDeviceType());
        //Checking if this is the user first time openning bot, if that is true we want to show him immediately welcome message with main menu.
        // Incoming class will not work in this case because user did not send any message instead we are going to parse user ViberID from default Viber log
        if (StringUtils.equals(incoming.getEvent(), START_MSG_EVENT)) {
            Gson gson = new Gson();
            Map jsonMap = gson.fromJson(text, Map.class);
            Map<String, String> userMap = (Map<String, String>) jsonMap.get("user");
            bot.messageForUser(userMap.get("id")).postText(WELCOME_MESSAGE, keyboardUtil.getMainMenu());
            logger.info("Showing welcome message.");
            return ResponseEntity.ok().build();
        }
        //TODO probaj ovo da ukljucis u switch isto
        //FIRST WE NEED TO CHECK IF USER IS AT LAST STAGE OF RESERVATION
        else if (httpUtil.getIsPayingStatus(userId)) {
            LocalDateTime startTime = dateUtil.parseUserInput(messageText);
            //CHECKING IF USER INPUT IS IN CORRECT FORM DAY.MONTH/HOUR:MIN
            if (startTime == null) {
                bot.messageForUser(userId).postText(ERROR_TIME, keyboardUtil.getMainMenu());
                httpUtil.changeIsPayingStatus(userId, false);
                logger.info("User failed to enter time in right format.");
            } else {
                //IF FORM IS CORRECT CHECKING IF TIMESLOT IS AVAILABLE
                Double totalMinutes = httpUtil.getTotalTime(userId);
                LocalDateTime endTime = dateUtil.setEndDate(startTime, totalMinutes);
                String checkTime = httpUtil.checkIfTimeIsAvailable(startTime, endTime);
                if (checkTime.equals("Time slot is available.")) {
                    OrderPOS sendOrderPOS = new OrderPOS(httpUtil.getCurrentList(userId), httpUtil.getTotalPrice(userId), startTime, endTime, "PENDING", userId);
                    httpUtil.sendOrder(sendOrderPOS, userId);
                    bot.messageForUser(userId).postText(SUCCESS_RESERVATION, keyboardUtil.getMainMenu());
                    httpUtil.changeIsPayingStatus(userId, false);
                    logger.info("Finishing reservation.");
                } else {
                    bot.messageForUser(userId).postText(checkTime);
                    logger.info("Time slot is not available giving user another chance to reserve.");
                }
            }

        }


        //If user is not in finishing phase we will go through bot menu flow
        else if (messageText.length() >= 3) {
            //First we have special case, if user accepted paying online  we don't wont to leave Viber without telling user that he will be redirected to payment page
            if(messageText.startsWith(assecoPaymentPage)){
                    bot.messageForUser(userId).postText(redirectPaymentMessage);
                }
            else {
                switch (messageText.substring(0, 3)) {
                    case selectCategoryFromMainMenu:
                        bot.messageForUser(userId).postKeyboard(keyboardUtil.setListMenu(messageText.substring(3)));
                        logger.info(String.format("Showing category list for %s", messageText.substring(3)));
                        break;
                    case addingItemToCart:
                        MenuItem addList = httpUtil.getItemByName(messageText.substring(3));
                        httpUtil.addServiceToCart(userId, addList);
                        bot.messageForUser(userId).postText("Dodajem na listu " + messageText.substring(3), keyboardUtil.getMainMenu());
                        logger.info("Adding to cart: " + messageText.substring(3));
                        break;
                    case navigateToCartMenu:
                        if (httpUtil.cartChecker(userId)) {
                            bot.messageForUser(userId).postKeyboard(keyboardUtil.setCartList(userId));
                            logger.info("Showing current cart.");
                        } else {
                            bot.messageForUser(userId).postText(ERROR_CART, keyboardUtil.getMainMenu());
                            logger.info("Unable to show current cart.");
                        }
                        break;
                    case startFinishProcess:
                        if (httpUtil.cartChecker(userId)) {
                            StringBuilder finishMsg = new StringBuilder(CHECK_CART);
                            for (String element : httpUtil.getCartList(userId)) {
                                finishMsg.append(element).append("\n");
                            }
                            bot.messageForUser(userId).postText(finishMsg.toString(), keyboardUtil.setYesNo());
                            logger.info("User have enough cart items to proceed with reservation.");
                        } else {
                            bot.messageForUser(userId).postText(ERROR_CART, keyboardUtil.getMainMenu());
                            logger.info("Unable to show current cart.");
                        }
                        break;
                    case startPaymentProcess:
                        if (httpUtil.cartChecker(userId)) {
                            bot.messageForUser(userId).postText(CHECK_PAYMENT, keyboardUtil.setPaymentOption(userId));
                            logger.info("Asking user if he agrees with his cart.");
                        } else {
                            bot.messageForUser(userId).postText(ERROR_CART, keyboardUtil.getMainMenu());
                            logger.info("Unable to show current cart.");
                        }
                        break;
                    case selectDeliveryTime:
                        bot.messageForUser(userId).postText(CHECK_TIME);
                        httpUtil.changeIsPayingStatus(userId, true);
                        logger.info("User selecting time.");
                        break;
                    case removingItemFromCart:
                        int newlineIndex = messageText.indexOf('\n', 3);
                        MenuItem rmvList = httpUtil.getItemByName(messageText.substring(3, newlineIndex));
                        httpUtil.removeCartItem(userId, rmvList);
                        bot.messageForUser(userId).postText("Uklanjam " + messageText.substring(3, newlineIndex), keyboardUtil.setCartList(userId));
                        logger.info("Trying to remove: " + messageText.substring(3));
                        break;
                    case navigateToMainMenu:
                        bot.messageForUser(userId).postKeyboard(keyboardUtil.getMainMenu());
                        logger.info("Navigating to main menu.");
                        break;
                    default:
                        bot.messageForUser(userId).postText("Komanda nije pronađena",keyboardUtil.getMainMenu());
                        logger.info("Navigating to main menu.");
                        break;
                }
            }
        } else {
            if (!StringUtils.equals(ignoreUserInput, messageText)) {
                bot.messageForUser(userId).postText("Komanda nije pronađena",keyboardUtil.getMainMenu());
                logger.info("Navigating to main menu.");
            }
        }

        return ResponseEntity.ok().build();
    }
    //TODO zameni viberbot za promenljivu iz StringUtils-a
    @RequestMapping(method = POST, path = "/viberbot/external-paying")
    ResponseEntity<?> sendExternalMessage(@RequestParam String viberId) throws UnsupportedEncodingException, URISyntaxException {
        ViberBot bot = ViberBotManager.viberBot(botToken);
        logger.info("GLEDAJ OVO: "+viberId);
        bot.messageForUser(viberId).postText(successfulPayment);
        bot.messageForUser(viberId).postText(CHECK_TIME);
        httpUtil.changeIsPayingStatus(viberId, true);
        logger.info("User successfully payed his bill.");
        logger.info("User selecting time.");
        return ResponseEntity.ok().build();

    }


    }
        

