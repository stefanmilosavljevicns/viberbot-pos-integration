package payten.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.payten.viberutil.keyboard.ViberButton;
import com.payten.viberutil.keyboard.ViberKeyboard;
import payten.bot.model.OrderPOS;
import payten.bot.model.ReservationSlot;


import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static payten.bot.util.BotConstants.*;

@Component
public class KeyboardUtil {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardUtil.class);
    @Autowired
    private HttpUtil httpUtil;
    private ViberKeyboard mainMenu;
    @Autowired
    private LocaleUtil localeUtil;
    @Autowired
    private StringUtils stringUtils;
    @Autowired
    private DateUtil dateUtil;
    private ViberKeyboard confirmationKeyboard;
    public ViberKeyboard getMainMenu(String locale) {
        mainMenu = new ViberKeyboard();
        mainMenu.setInputFieldState("hidden");
        mainMenu.setType("keyboard");
        mainMenu.addButton(new ViberButton(selectDurationOfReservation)
                .setText(String.format("<font color='#ffffff'><b>%s</b></font>",localeUtil.getLocalizedMessage("message.reserve-table",locale)))
                .setImage(stringUtils.getIconReserve())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                .setTextVAlign(ViberButton.TextAlign.BOTTOM)
                .setBgColor(stringUtils.getPrimarilyColor())
                .setColumns(3)
                .setSilent(true)
                .setRows(2));
        mainMenu.addButton(new ViberButton(aboutUs)
                .setText(String.format("<font color='#ffffff'><b>%s</b></font>",localeUtil.getLocalizedMessage("message.about-us",locale)))
                .setImage(stringUtils.getIconAboutUs())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                .setTextVAlign(ViberButton.TextAlign.BOTTOM)
                .setColumns(3)
                .setSilent(true)
                .setRows(2));
        mainMenu.addButton(new ViberButton(changeLanguageMenu)
                .setText(String.format("<font color='#ffffff'><b>%s</b></font>",localeUtil.getLocalizedMessage("message.choose-language",locale)))
                .setImage(stringUtils.getIconChooseLanguage())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                .setTextVAlign(ViberButton.TextAlign.BOTTOM)
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setColumns(3)
                .setSilent(true)
                .setRows(2));
        mainMenu.addButton(new ViberButton(historyOfReservation)
                .setText(String.format("<font color='#ffffff'><b>%s</b></font>",localeUtil.getLocalizedMessage("message.reservation-history",locale)))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                .setTextVAlign(ViberButton.TextAlign.BOTTOM)
                .setImage(stringUtils.getIconPreviousOrders())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getPrimarilyColor())
                .setColumns(3)
                .setSilent(true)
                .setRows(2));
        logger.info("Main Menu has been successfully generated!");
        return mainMenu;

    }
    public ViberKeyboard pickTimeSlot(String paramBody,String locale) throws URISyntaxException, JsonProcessingException {
        ViberKeyboard reservationDuration = new ViberKeyboard();
        reservationDuration.setInputFieldState("hidden");
        reservationDuration.setType("keyboard");
        List<ReservationSlot> listOfAvailableTimeSlots = httpUtil.getAvailableTimeSlots(paramBody);
        for(ReservationSlot date : listOfAvailableTimeSlots){
            reservationDuration.addButton(new ViberButton(sendOrderToPOS+date.getStartDate().toString()+"/"+date.getEndDate().toString()+"/"+date.getTable())
                    .setText(String.format(stringUtils.getButtonStandard(),date.getStartDate().getHour()+":"+date.getStartDate().getMinute()+"-"+date.getEndDate().getHour()+":"+date.getEndDate().getMinute()))
                    .setTextSize(ViberButton.TextSize.LARGE)
                    .setBgColor(stringUtils.getSecondarilyColor())
                    .setSilent(true)
                    .setColumns(6)
                    .setRows(1));
        }
        return reservationDuration;
    }
    public ViberKeyboard pickReservationDay(Integer duration,String locale) throws URISyntaxException {
        ViberKeyboard reservationDuration = new ViberKeyboard();
        reservationDuration.setInputFieldState("hidden");
        reservationDuration.setType("keyboard");
        List<LocalDate> listOfAvailableDays = httpUtil.getAvailableDays(duration);
        for(LocalDate date : listOfAvailableDays){
            reservationDuration.addButton(new ViberButton(listAvailableTimeSlot+date.toString()+":"+duration)
                    .setText(String.format(stringUtils.getButtonStandard(),dateUtil.translateDayValue(date.getDayOfWeek(),locale)+" "+date.getDayOfMonth()+"."+date.getMonthValue()+"."))
                    .setTextSize(ViberButton.TextSize.LARGE)
                    .setBgColor(stringUtils.getSecondarilyColor())
                    .setSilent(true)
                    .setColumns(6)
                    .setRows(1));
        }
        return reservationDuration;
    }

    public ViberKeyboard pickReservationDuration(String locale){
        ViberKeyboard reservationDuration = new ViberKeyboard();
        reservationDuration.setInputFieldState("hidden");
        reservationDuration.setType("keyboard");
        reservationDuration.addButton(new ViberButton(selectDayReservation+30)
                .setText(String.format(stringUtils.getButtonStandard(),localeUtil.getLocalizedMessage("message.30-minutes",locale)))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setSilent(true)
                .setColumns(6)
                .setRows(1));
        reservationDuration.addButton(new ViberButton(selectDayReservation+60)
                .setText(String.format(stringUtils.getButtonStandard(),localeUtil.getLocalizedMessage("message.1-hour",locale)))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getPrimarilyColor())
                .setSilent(true)
                .setColumns(6)
                .setRows(1));
        reservationDuration.addButton(new ViberButton(selectDayReservation+120)
                .setText(String.format(stringUtils.getButtonStandard(),localeUtil.getLocalizedMessage("message.2-hours",locale)))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setSilent(true)
                .setColumns(6)
                .setRows(1));
        reservationDuration.addButton(new ViberButton(selectDayReservation+180)
                .setText(String.format(stringUtils.getButtonStandard(),localeUtil.getLocalizedMessage("message.3-hours",locale)))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getPrimarilyColor())
                .setSilent(true)
                .setColumns(6)
                .setRows(1));
        reservationDuration.addButton(new ViberButton(navigateToMainMenu)
                .setText(localeUtil.getLocalizedMessage("message.return-main-menu",locale))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setSilent(true)
                .setColumns(6)
                .setRows(1));
        return reservationDuration;
    }
    public ViberKeyboard setYesNo(){
        confirmationKeyboard = new ViberKeyboard();
        confirmationKeyboard.setInputFieldState("hidden");
        confirmationKeyboard.setType("keyboard");
        confirmationKeyboard.addButton(new ViberButton(agreeWithCart)
                .setText(stringUtils.getButtonYes())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getPrimarilyColor())
                .setColumns(3)
                .setSilent(true)
                .setRows(2));
        confirmationKeyboard.addButton(new ViberButton(navigateToMainMenu)
                .setText(stringUtils.getButtonNo())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setSilent(true)
                .setColumns(3)
                .setRows(2));
        return confirmationKeyboard;
    }
    public ViberKeyboard historyOfReservationKeyboard(String viberId,String userLocale) throws URISyntaxException {
        ArrayList<OrderPOS> listOfReservations = httpUtil.getHistoryOfOrders(viberId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        ViberKeyboard historyOfReservationKeyboard = new ViberKeyboard();
        historyOfReservationKeyboard.setInputFieldState("hidden");
        historyOfReservationKeyboard.setType("keyboard");
        if(!listOfReservations.isEmpty()){
            for(OrderPOS reservationItem:listOfReservations){
                String formattingReservations = reservationItem.getStartTime().format(formatter) + "\n" + reservationItem.getState();
                historyOfReservationKeyboard.addButton(new ViberButton(ignoreUserInput)
                        .setBgColor(stringUtils.getSecondarilyColor())
                        .setText(String.format(stringUtils.getButtonStandard(), formattingReservations))
                        .setColumns(4)
                        .setRows(1)
                        .setSilent(true)
                        .setTextSize(ViberButton.TextSize.MEDIUM)
                        .setTextHAlign(ViberButton.TextAlign.LEFT));
                historyOfReservationKeyboard.addButton(new ViberButton(ignoreUserInput)
                        .setImage(stringUtils.getCoffeMug())
                        .setBgColor(stringUtils.getPrimarilyColor())
                        .setColumns(2)
                        .setRows(1)
                        .setSilent(true)
                        .setTextSize(ViberButton.TextSize.MEDIUM).setSilent(true)
                        .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                        .setTextVAlign(ViberButton.TextAlign.MIDDLE));
            }
        }
        historyOfReservationKeyboard.addButton(new ViberButton(navigateToMainMenu)
                .setText(String.format(stringUtils.getButtonStandard(),localeUtil.getLocalizedMessage("message.return-main-menu", userLocale)))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setSilent(true)
                .setRows(1)
                .setBgColor(stringUtils.getSecondarilyColor()));
        return historyOfReservationKeyboard;
    }
    public ViberKeyboard changeLanguage(String userLocale){
        ViberKeyboard changeLanguage = new ViberKeyboard();
        changeLanguage.setInputFieldState("hidden");
        changeLanguage.setType("keyboard");
        changeLanguage.addButton(new ViberButton(changeUserLocaleSrb)
                .setText(stringUtils.getButtonYes())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setText("<font color='#ffffff'><b>Srpski</b></font>")
                .setImage(stringUtils.getSerbianFlag())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                .setTextVAlign(ViberButton.TextAlign.BOTTOM)
                .setColumns(2)
                .setSilent(true)
                .setRows(2));
        changeLanguage.addButton(new ViberButton(changeUserLocaleEng)
                .setText(stringUtils.getButtonNo())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setText("<font color='#ffffff'><b>English</b></font>")
                .setImage(stringUtils.getUkFlag())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                .setTextVAlign(ViberButton.TextAlign.BOTTOM)
                .setSilent(true)
                .setColumns(2)
                .setRows(2));
        changeLanguage.addButton(new ViberButton(changeUserLocaleRus)
                .setText(stringUtils.getButtonNo())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setText("<font color='#ffffff'><b>Русский</b></font>")
                .setImage(stringUtils.getRussianFlag())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                .setTextVAlign(ViberButton.TextAlign.BOTTOM)
                .setSilent(true)
                .setColumns(2)
                .setRows(2));
        changeLanguage.addButton(new ViberButton(navigateToMainMenu)
                .setText(String.format(stringUtils.getButtonStandard(),localeUtil.getLocalizedMessage("message.return-main-menu", userLocale)))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setSilent(true)
                .setBgColor(stringUtils.getSecondarilyColor()));
        return changeLanguage;
    }
}
