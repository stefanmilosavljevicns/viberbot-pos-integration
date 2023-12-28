package payten.bot.controller;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payten.bot.model.OrderPOS;
import payten.bot.util.DateUtil;
import payten.bot.util.HttpUtil;
import payten.bot.util.KeyboardUtil;
import com.payten.viberutil.ViberBot;
import com.payten.viberutil.ViberBotManager;
import com.payten.viberutil.incoming.Incoming;
import com.payten.viberutil.incoming.IncomingImpl;

import java.io.IOException;
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
        String senderName = incoming.getSenderName();
        String messageText = incoming.getMessageText();
        if (!StringUtils.equals(eventType, MESSAGE_EVENT) && !StringUtils.equals(incoming.getEvent(), START_MSG_EVENT))
            return ResponseEntity.ok().build();
        if (StringUtils.equals(incoming.getEvent(), START_MSG_EVENT)) {
            Gson gson = new Gson();
            Map jsonMap = gson.fromJson(text, Map.class);
            Map<String, String> userMap = (Map<String, String>) jsonMap.get("user");
            bot.messageForUser(userMap.get("id")).postText(stringUtils.getMessageWelcome(), keyboardUtil.getMainMenu());
            logger.info(String.format(controlerLogFormat, "Showing welcome message.", userId));
            return ResponseEntity.ok().build();
        } else if (messageText.length() >= 3) {
            switch (messageText.substring(0, 3)) {
                case aboutUs:
                    bot.messageForUser(userId).postPicture(stringUtils.getImageAboutUs1(),"");
                    bot.messageForUser(userId).postPicture(stringUtils.getImageAboutUs2(),"");
                    bot.messageForUser(userId).postText(stringUtils.getMessageAboutUs(), keyboardUtil.getMainMenu());
                    break;
                case changeLanguage:
                    bot.messageForUser(userId).postText("Izaberite jezik",keyboardUtil.changeLanguage());
                    break;
                case startReservationProcess:
                    if (httpUtil.cartChecker(userId)) {
                        StringBuilder finishMsg = new StringBuilder(stringUtils.getMessageCheckCart());
                        bot.messageForUser(userId).postText(finishMsg.toString(), keyboardUtil.setYesNo());
                        logger.info(String.format(controlerLogFormat, "User have enough cart items to proceed with reservation.", userId));

                    } else {
                        bot.messageForUser(userId).postText(stringUtils.getMessageError(), keyboardUtil.getMainMenu());
                        logger.info(String.format(controlerLogFormat, "Unable to show current cart.", userId));
                    }
                    break;
                case agreeWithCart:
                    if (httpUtil.cartChecker(userId)) {
                        bot.messageForUser(userId).postText(stringUtils.getMessageCheckTime(), keyboardUtil.setDayPicker());
                        logger.info(String.format(controlerLogFormat, "Asking user if he agrees with his cart.", userId));
                    } else {
                        bot.messageForUser(userId).postText(stringUtils.getMessageError(), keyboardUtil.getMainMenu());
                        logger.info(String.format(controlerLogFormat, "Unable to show current cart.", userId));
                    }
                    break;
                case selectDayReservation:
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate localDate = LocalDate.parse(messageText.substring(3), formatter);
                    List<LocalDateTime> allSlots = httpUtil.checkFreeTimeSlots(localDate, httpUtil.getTotalTime(userId).intValue());
                    bot.messageForUser(userId).postText("Izaberite termin kako bi nastavili sa rezervacijom: ", keyboardUtil.setHourPicker(allSlots));
                    logger.info(String.format(controlerLogFormat, "Showing user list of free time slots.", userId));

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
                        bot.messageForUser(userId).postText(stringUtils.getMessageSuccessReservation(), keyboardUtil.getMainMenu());
                        logger.info(String.format(controlerLogFormat, "Session finished, clearing cart.", userId));
                    } else {
                        bot.messageForUser(userId).postText(stringUtils.getMessageErrorTime(), keyboardUtil.getMainMenu());
                        logger.info(String.format(controlerLogFormat, "Error in reservations.", userId));
                    }
                    break;
                case navigateToMainMenu:
                    bot.messageForUser(userId).postKeyboard(keyboardUtil.getMainMenu());
                    logger.info(String.format(controlerLogFormat, "Navigating to main menu." + messageText.substring(3), userId));
                    break;
            }
        } else {
            if (!StringUtils.equals(ignoreUserInput, messageText)) {
                bot.messageForUser(userId).postText(stringUtils.getMessageUnknownCommand(), keyboardUtil.getMainMenu());
                logger.info(String.format(controlerLogFormat, "Navigating to main menu.", userId));

            }
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("${viber.bot-path}" + "/updateStartTime")
    ResponseEntity<?> updateStartTime(@RequestParam("startDate") String start, @RequestParam("viberId") String viberId) throws URISyntaxException {
        ViberBot bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        httpUtil.updateStartTime(viberId, start);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        bot.messageForUser(viberId).postText(stringUtils.getMessageReservationUpdate() + startDate.getDayOfMonth() + "." + startDate.getMonthValue() + ". u " + startDate.getHour() + ":" + startDate.getMinute(), keyboardUtil.getMainMenu());
        logger.info(String.format(controlerLogFormat, "We are sending user information that merchant changed his start time", viberId));

        return ResponseEntity.ok().build();

    }
}
