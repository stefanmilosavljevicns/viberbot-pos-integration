package paytenfood.bot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paytenfood.bot.model.MenuItem;
import paytenfood.bot.model.OrderPOS;
import paytenfood.bot.util.DateUtil;
import paytenfood.bot.util.HttpUtil;
import paytenfood.bot.util.KeyboardUtil;
import ru.multicon.viber4j.ViberBot;
import ru.multicon.viber4j.ViberBotManager;
import ru.multicon.viber4j.incoming.Incoming;
import ru.multicon.viber4j.incoming.IncomingImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static paytenfood.bot.util.BotConstants.*;

@RestController
public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    public paytenfood.bot.util.StringUtils stringUtils;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private KeyboardUtil keyboardUtil;

    @RequestMapping(method = POST, path = "${viber.bot-path}")
    ResponseEntity<?> callbackHandle(@RequestBody String text) throws IOException, InterruptedException, URISyntaxException {
        logger.info("Received messageForUser {}", text);
        // Processing incoming messageForUser
        ViberBot bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        Incoming incoming = IncomingImpl.fromString(text);
        String eventType = incoming.getEvent();
        //
        // getting info about user
        //
        String userId = incoming.getSenderId();
        String messageText = incoming.getMessageText();

        if (!StringUtils.equals(eventType, MESSAGE_EVENT) && !StringUtils.equals(incoming.getEvent(), START_MSG_EVENT))
            return ResponseEntity.ok().build();

        //  Checking if this is the user first time opening Viber Bot, if that is true we want to show him immediately welcome message with main menu.
        // Incoming class will not work in this case because user did not send any message instead we are going to parse user ViberID from default Viber log
        if (StringUtils.equals(incoming.getEvent(), START_MSG_EVENT)) {
            Gson gson = new Gson();
            Map jsonMap = gson.fromJson(text, Map.class);
            Map<String, String> userMap = (Map<String, String>) jsonMap.get("user");
            bot.messageForUser(userMap.get("id")).postText(stringUtils.getMessageWelcome(), keyboardUtil.getMainMenu());
            logger.info("Showing welcome message.");
            return ResponseEntity.ok().build();
        }
        //Handling user input here
        else if (messageText.length() >= 3) {
                //This IF will trigger when user is on asseco payment page. If something happens: browser crashed, accidentaly closing browser etc... We want to handle this scenario by giving him  chance to restart payment flow.
                if (messageText.startsWith(assecoPaymentPage)){
                    bot.messageForUser(userId).postKeyboard(keyboardUtil.returnToPayment());
                    logger.info("Returning user back to payment option");
                    return ResponseEntity.ok().build();
                }
                //Going through Viber bot flow, using our 3 Character keywords for mapping different actions.
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
                            bot.messageForUser(userId).postText(stringUtils.getMessageError(), keyboardUtil.getMainMenu());
                            logger.info("Unable to show current cart.");
                        }
                        break;
                        //First thing that triggers after user selected FINISH RESERVATION button. First we are going to show him what items did he selected.
                    case startReservationProcess:
                        if (httpUtil.cartChecker(userId)) {
                            StringBuilder finishMsg = new StringBuilder(stringUtils.getMessageCheckCart());
                            for (String element : httpUtil.getCartList(userId)) {
                                finishMsg.append(element).append("\n");
                            }
                            bot.messageForUser(userId).postText(finishMsg.toString(), keyboardUtil.setYesNo());
                            logger.info("User have enough cart items to proceed with reservation.");
                        } else {
                            bot.messageForUser(userId).postText(stringUtils.getMessageError(), keyboardUtil.getMainMenu());
                            logger.info("Unable to show current cart.");
                        }
                        break;
                        //If the user agrees with his cart we are going to ask him to choose time.
                    case startPaymentProcess:
                        if (httpUtil.cartChecker(userId)) {
                            bot.messageForUser(userId).postText(stringUtils.getMessageCheckTime());
                            logger.info("Asking user if he agrees with his cart.");
                        } else {
                            bot.messageForUser(userId).postText(stringUtils.getMessageError(), keyboardUtil.getMainMenu());
                            logger.info("Unable to show current cart.");
                        }
                        break;
                    case selectDeliveryTime:
                        bot.messageForUser(userId).postText(stringUtils.getMessageCheckTime());
                        logger.info("User selecting time.");
                        break;
                    case clearCartAndFinishSession:
                        bot.messageForUser(userId).postText(stringUtils.getMessageSuccessReservation(),keyboardUtil.getMainMenu());
                        httpUtil.clearCart(userId);
                        logger.info("Session finished, clearing cart.");
                        break;
                    case removingItemFromCart:
                        int newlineIndex = messageText.indexOf('\n', 3);
                        MenuItem rmvList = httpUtil.getItemByName(messageText.substring(3, newlineIndex));
                        httpUtil.removeServiceFromCart(userId, rmvList);
                        bot.messageForUser(userId).postText("Uklanjam " + messageText.substring(3, newlineIndex), keyboardUtil.setCartList(userId));
                        logger.info("Trying to remove: " + messageText.substring(3));
                        break;
                    case navigateToMainMenu:
                        bot.messageForUser(userId).postKeyboard(keyboardUtil.getMainMenu());
                        logger.info("Navigating to main menu.");
                        break;
                    case ignoreUserInput:
                        logger.info("User clicked on text label, safely ignore this log.");
                        break;
                        //If input string doesn't match with our 3 characters keywords, that means that user is picking time for reservation. That flow will be handled in default part of SWITCH
                    default:
                        LocalDateTime startTime = dateUtil.parseUserInput(messageText);
                        if(startTime != null){
                            //IF FORM IS CORRECT CHECKING IF TIMESLOT IS AVAILABLE
                            Double totalMinutes = httpUtil.getTotalTime(userId);
                            LocalDateTime endTime = dateUtil.setEndDate(startTime, totalMinutes);
                            String checkTime = httpUtil.checkIfTimeIsAvailable(startTime, endTime);
                            //Checking time availability
                            if (checkTime.equals("Time slot is available.")) {
                                OrderPOS sendOrderPOS = new OrderPOS(httpUtil.getCurrentList(userId), httpUtil.getTotalPrice(userId), startTime, endTime, "PENDING", userId);
                                httpUtil.sendOrder(sendOrderPOS, userId);
                                bot.messageForUser(userId).postText(stringUtils.getMessagePaymentOnline(), keyboardUtil.setPaymentOption(userId));
                                logger.info("Finishing reservation.");
                            }
                            //If user missed form we will redirect him to main menu
                            else {
                                bot.messageForUser(userId).postText(checkTime);
                                logger.info("Time slot is not available giving user another chance to reserve.");
                            }
                        }
                        //Incorrect form giving user another chance to enter correct
                        else{
                            bot.messageForUser(userId).postText("Pogrešan unos vremena, pokušajte ponovo.",keyboardUtil.getMainMenu());
                            logger.info("User failed to enter right format for time.");
                        }
                        break;
                }

        } else {
            if (!StringUtils.equals(ignoreUserInput, messageText)) {
                bot.messageForUser(userId).postText("Komanda nije pronađena", keyboardUtil.getMainMenu());
                logger.info("Navigating to main menu.");
            }
        }

        return ResponseEntity.ok().build();
    }
    //This endpoint will be called by our page, it is being used for letting bot know if payment is OK or NOT
    @RequestMapping(method = POST, path = "${viber.bot-path}" + "/external-paying")
    ResponseEntity<?> sendExternalSuccess(@RequestParam String viberId) throws UnsupportedEncodingException, URISyntaxException {
        ViberBot bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        bot.messageForUser(viberId).postText(stringUtils.getMessageSuccessReservation(),keyboardUtil.getMainMenu());
        httpUtil.clearCart(viberId);
        logger.info("User successfully payed his bill, clearing cart.");
        return ResponseEntity.ok().build();

    }

    @RequestMapping(method = POST, path = "${viber.bot-path}" + "/external-failure")
    ResponseEntity<?> sendExternalFail(@RequestParam String viberId) throws UnsupportedEncodingException, URISyntaxException, JsonProcessingException {
        ViberBot bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        bot.messageForUser(viberId).postText(failedPayment, keyboardUtil.setPaymentOption(viberId));
        logger.info("User failed to complete online payment, trying again.");
        return ResponseEntity.ok().build();

    }

    @PutMapping("${viber.bot-path}" + "/updateStartTime")
    ResponseEntity<?> updateStartTime(@RequestParam("startDate") String start, @RequestParam("viberId") String viberId) throws UnsupportedEncodingException, URISyntaxException, JsonProcessingException {        
        ViberBot bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        httpUtil.updateStartTime(viberId,start);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        bot.messageForUser(viberId).postText("Vaš zakazani termin je promenjen, vreme novog termina je: " + startDate.getDayOfMonth()+"."+startDate.getMonthValue() + startDate.getHour()+":"+startDate.getMinute(), keyboardUtil.getMainMenu());
        logger.info("We are sending user information that merchant changed his start time");
        return ResponseEntity.ok().build();

    }
}