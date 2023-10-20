package payten.bot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payten.bot.model.MenuItem;
import payten.bot.model.OrderPOS;
import payten.bot.util.DateUtil;
import payten.bot.util.HttpUtil;
import payten.bot.util.KeyboardUtil;
import com.payten.viberutil.ViberBot;
import com.payten.viberutil.ViberBotManager;
import com.payten.viberutil.incoming.Incoming;
import com.payten.viberutil.incoming.IncomingImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static payten.bot.util.BotConstants.*;

@RestController
public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    public payten.bot.util.StringUtils stringUtils;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private KeyboardUtil keyboardUtil;

    @RequestMapping(method = POST, path = "${viber.bot-path}")
    ResponseEntity<?> callbackHandle(@RequestBody String text) throws IOException, InterruptedException, URISyntaxException {
        logger.info("Received messageForUser {}", text);
        ViberBot bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        Incoming incoming = IncomingImpl.fromString(text);
        String eventType = incoming.getEvent();
        String userId = incoming.getSenderId();
        String messageText = incoming.getMessageText();
        if (!StringUtils.equals(eventType, MESSAGE_EVENT) && !StringUtils.equals(incoming.getEvent(), START_MSG_EVENT))
            return ResponseEntity.ok().build();
        if (StringUtils.equals(incoming.getEvent(), START_MSG_EVENT)) {
            Gson gson = new Gson();
            Map jsonMap = gson.fromJson(text, Map.class);
            Map<String, String> userMap = (Map<String, String>) jsonMap.get("user");
            bot.messageForUser(userMap.get("id")).postText(stringUtils.getMessageWelcome(), keyboardUtil.getMainMenu());
            logger.info("Showing welcome message.");
            return ResponseEntity.ok().build();
        }
        else if (messageText.length() >= 3) {
                switch (messageText.substring(0, 3)) {
                    case selectCategoryFromMainMenu:
                        bot.messageForUser(userId).postKeyboard(keyboardUtil.setListMenu(messageText.substring(3)));
                        logger.info(String.format("Showing category list for %s", messageText.substring(3)));
                        break;
                    case addingItemToCart:
                        MenuItem addList = httpUtil.getItemByName(messageText.substring(3));
                        httpUtil.addServiceToCart(userId, addList);
                        bot.messageForUser(userId).postText("Adding to list: " + messageText.substring(3), keyboardUtil.getMainMenu());
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
                    case startPaymentProcess:
                        if (httpUtil.cartChecker(userId)) {
                            bot.messageForUser(userId).postText(stringUtils.getMessageCheckTime(), keyboardUtil.setDayPicker());
                            logger.info("Asking user if he agrees with his cart.");
                        } else {
                            bot.messageForUser(userId).postText(stringUtils.getMessageError(), keyboardUtil.getMainMenu());
                            logger.info("Unable to show current cart.");
                        }
                        break;
                    case selectDayReservation:
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        LocalDate localDate = LocalDate.parse(messageText.substring(3), formatter);
                        List<LocalDateTime> allSlots = httpUtil.checkFreeTimeSlots(localDate,httpUtil.getTotalTime(userId).intValue());
                        bot.messageForUser(userId).postText("Select time slot in order to finish reservation: ",keyboardUtil.setHourPicker(allSlots));
                        logger.info("Showing user: " + userId +"list of free time slots.");
                        break;
                    case selectDeliveryTime:
                        bot.messageForUser(userId).postText(stringUtils.getMessageCheckTime());
                        logger.info("User selecting time.");
                        break;
                    case sendOrderToPOS:
                            LocalDateTime startTime = LocalDateTime.parse(messageText.substring(3));
                            Double totalMinutes = httpUtil.getTotalTime(userId);
                            LocalDateTime endTime = dateUtil.setEndDate(startTime, totalMinutes);
                            String checkTime = httpUtil.checkIfTimeIsAvailable(startTime, endTime);
                            if (checkTime.equals("Time slot is available.")) {
                                OrderPOS sendOrderPOS = new OrderPOS(httpUtil.getCurrentList(userId), httpUtil.getTotalPrice(userId), startTime, endTime, "PENDING", userId);
                                httpUtil.sendOrder(sendOrderPOS, userId);
                                bot.messageForUser(userId).postText(stringUtils.getMessageSuccessReservation(),keyboardUtil.getMainMenu());
                                httpUtil.clearCart(userId);
                                logger.info("Session finished, clearing cart.");
                            }
                            else{
                                bot.messageForUser(userId).postText("Došlo je do greške ili je termin upravo neko rezervisao pre Vas, vraćam Vas na početak",keyboardUtil.getMainMenu());
                            }
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
                        bot.messageForUser(userId).postText("Removing " + messageText.substring(3, newlineIndex), keyboardUtil.setCartList(userId));
                        logger.info("Trying to remove: " + messageText.substring(3));
                        break;
                    case navigateToMainMenu:
                        bot.messageForUser(userId).postKeyboard(keyboardUtil.getMainMenu());
                        logger.info("Navigating to main menu.");
                        break;
                    case ignoreUserInput:
                        logger.info("User clicked on text label, safely ignore this log.");
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
    @PutMapping("${viber.bot-path}" + "/updateStartTime")
    ResponseEntity<?> updateStartTime(@RequestParam("startDate") String start, @RequestParam("viberId") String viberId) throws URISyntaxException {
        ViberBot bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        httpUtil.updateStartTime(viberId,start);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        bot.messageForUser(viberId).postText("Your time slot has been updated to: " + startDate.getDayOfMonth()+"."+startDate.getMonthValue()+". u " + startDate.getHour()+":"+startDate.getMinute(), keyboardUtil.getMainMenu());
        logger.info("We are sending user information that merchant changed his start time");
        return ResponseEntity.ok().build();

    }
}