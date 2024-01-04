package payten.bot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payten.bot.util.DateUtil;
import payten.bot.util.HttpUtil;
import payten.bot.util.KeyboardUtil;
import com.payten.viberutil.ViberBot;
import com.payten.viberutil.ViberBotManager;
import com.payten.viberutil.incoming.Incoming;
import com.payten.viberutil.incoming.IncomingImpl;
import payten.bot.util.LocaleUtil;

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
    private LocaleUtil localeUtil;
    @Autowired
    public payten.bot.util.StringUtils stringUtils;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private KeyboardUtil keyboardUtil;
    String userLocale;
    String eventType;
    String userId;
    String senderName;
    String messageText;
    ViberBot bot;

    @RequestMapping(method = POST, path = "${viber.bot-path}")
    ResponseEntity<?> callbackHandle(@RequestBody String text) throws IOException, InterruptedException, URISyntaxException {
        logger.info("Received messageForUser {}", text);
        bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        Incoming incoming = IncomingImpl.fromString(text);
        eventType = incoming.getEvent();
        userId = incoming.getSenderId();
        senderName = incoming.getSenderName();
        messageText = incoming.getMessageText();
        if (!StringUtils.equals(eventType, MESSAGE_EVENT) && !StringUtils.equals(incoming.getEvent(), START_MSG_EVENT))
            return ResponseEntity.ok().build();
        if (StringUtils.equals(incoming.getEvent(), START_MSG_EVENT)) {
            Gson gson = new Gson();
            Map jsonMap = gson.fromJson(text, Map.class);
            Map<String, String> userMap = (Map<String, String>) jsonMap.get("user");
            userLocale = httpUtil.getUserLocale(userMap.get("id"));
            bot.messageForUser(userMap.get("id")).postText(localeUtil.getLocalizedMessage("message.welcome",userLocale), keyboardUtil.getMainMenu(userLocale));
            logger.info(String.format(controlerLogFormat, "Showing welcome message.", userId));
            return ResponseEntity.ok().build();
        } else if (messageText.length() >= 3) {
            userLocale = httpUtil.getUserLocale(userId);
            switch (messageText.substring(0, 3)) {
                case selectDayReservation:
                    bot.messageForUser(userId).postText(localeUtil.getLocalizedMessage("message.choose-reservation-day", userLocale), keyboardUtil.pickReservationDay(Integer.parseInt(messageText.substring(3)),userLocale));
                    break;
                case selectDurationOfReservation:
                    bot.messageForUser(userId).postText(localeUtil.getLocalizedMessage("message.choose-reservation-duration", userLocale), keyboardUtil.pickReservationDuration(userLocale));
                    break;
                case aboutUs:
                    bot.messageForUser(userId).postText(localeUtil.getLocalizedMessage("message.about-us-description", userLocale), keyboardUtil.getMainMenu(userLocale));
                    break;
                case changeLanguageMenu:
                    bot.messageForUser(userId).postKeyboard(keyboardUtil.changeLanguage(userLocale));
                    break;
                case changeUserLocaleSrb:
                    httpUtil.changeUserLocale(userId,"SRB");
                    userLocale = "SRB";
                    bot.messageForUser(userId).postKeyboard(keyboardUtil.getMainMenu(userLocale));
                    logger.info(String.format(controlerLogFormat, "Changing locale to Serbian.", userId));
                    break;
                case changeUserLocaleRus:
                    httpUtil.changeUserLocale(userId,"RUS");
                    userLocale = "RUS";
                    bot.messageForUser(userId).postKeyboard(keyboardUtil.getMainMenu(userLocale));
                    logger.info(String.format(controlerLogFormat, "Changing locale to Russian.", userId));
                    break;
                case changeUserLocaleEng:
                    httpUtil.changeUserLocale(userId,"ENG");
                    userLocale = "ENG";
                    bot.messageForUser(userId).postKeyboard(keyboardUtil.getMainMenu(userLocale));
                    logger.info(String.format(controlerLogFormat, "Changing locale to English.", userId));
                    break;
                case historyOfReservation:
                    bot.messageForUser(userId).postKeyboard(keyboardUtil.historyOfReservationKeyboard(userId,userLocale));
                    logger.info(String.format(controlerLogFormat, "Displaying user his history.", userId));
                    break;
                case navigateToMainMenu:
                    bot.messageForUser(userId).postKeyboard(keyboardUtil.getMainMenu(userLocale));
                    logger.info(String.format(controlerLogFormat, "Navigating to main menu." + messageText.substring(3), userId));
                    break;
            }
        } else {
            if (!StringUtils.equals(ignoreUserInput, messageText)) {
                bot.messageForUser(userId).postText(stringUtils.getMessageUnknownCommand(), keyboardUtil.getMainMenu(userLocale));
                logger.info(String.format(controlerLogFormat, "Navigating to main menu.", userId));

            }
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("${viber.bot-path}" + "/updateStartTime")
    ResponseEntity<?> updateStartTime(@RequestParam("startDate") String start, @RequestParam("viberId") String viberId) throws URISyntaxException, JsonProcessingException {
        bot = ViberBotManager.viberBot(stringUtils.getBotToken());
        httpUtil.updateStartTime(viberId, start);
        String locale = httpUtil.getUserLocale(viberId);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        bot.messageForUser(viberId).postText(stringUtils.getMessageReservationUpdate() + startDate.getDayOfMonth() + "." + startDate.getMonthValue() + ". u " + startDate.getHour() + ":" + startDate.getMinute(), keyboardUtil.getMainMenu(locale));
        logger.info(String.format(controlerLogFormat, "We are sending user information that merchant changed his start time", viberId));

        return ResponseEntity.ok().build();

    }
}
