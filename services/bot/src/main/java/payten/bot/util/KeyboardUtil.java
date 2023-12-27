package payten.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.payten.viberutil.keyboard.ViberButton;
import com.payten.viberutil.keyboard.ViberKeyboard;


import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static payten.bot.util.BotConstants.*;

@Component
public class KeyboardUtil {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardUtil.class);
    int maxCategories;
    @Autowired
    private HttpUtil httpUtil;
    private ViberKeyboard mainMenu;
    @Autowired
    private StringUtils stringUtils;
    @Autowired
    private DateUtil dateUtil;
    private ViberKeyboard confirmationKeyboard;
    public ViberKeyboard getMainMenu() {
        return mainMenu;
    }
    public ViberKeyboard setListMenu(String listName) throws JsonProcessingException {
        ViberKeyboard listMenu = new ViberKeyboard();
        listMenu.setInputFieldState("hidden");
        listMenu.setType("keyboard");
        listMenu.addButton(new ViberButton(navigateToMainMenu)
                    .setText(String.format(stringUtils.getButtonStandard(),stringUtils.getMessageReturnToMenu()))
                    .setTextSize(ViberButton.TextSize.LARGE)
                    .setBgColor(stringUtils.getPrimarilyColor())
                    .setSilent(true));
        return listMenu;
    }
    public ViberKeyboard setDayPicker(){
        List<LocalDateTime> availableDays = dateUtil.getWorkingWeekDates();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        ViberKeyboard dayPicker = new ViberKeyboard();
        dayPicker.setInputFieldState("hidden");
        boolean ticker = true;
        dayPicker.setType("keyboard");
        for (LocalDateTime availableDay : availableDays) {
            String formattedDate = availableDay.format(formatter);
            ViberButton viberButton = new ViberButton((String.format(selectDayReservation + "%s", formattedDate)))
                    .setText(String.format(stringUtils.getButtonStandard(), dateUtil.translateDayValue(availableDay.getDayOfWeek().getValue()) + " (" + availableDay.getDayOfMonth() + "." + availableDay.getMonthValue() + ")"))
                    .setColumns(6)
                    .setRows(1)
                    .setSilent(true)
                    .setTextSize(ViberButton.TextSize.MEDIUM)
                    .setTextHAlign(ViberButton.TextAlign.MIDDLE);
            if(ticker){
                viberButton.setBgColor(stringUtils.getPrimarilyColor());
                ticker = false;
            }
            else{
                viberButton.setBgColor(stringUtils.getSecondarilyColor());
                ticker = true;
            }
            dayPicker.addButton(viberButton);

        }
        dayPicker.addButton(new ViberButton(navigateToMainMenu)
                .setText(String.format(stringUtils.getButtonStandard(),stringUtils.getMessageReturnToMenu()))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setSilent(true)
                .setBgColor(stringUtils.getPrimarilyColor()));
        logger.info(availableDays.toString());
        return dayPicker;
    }
    public ViberKeyboard setHourPicker(List<LocalDateTime> freeTimeSlots){
        ViberKeyboard hourPicker = new ViberKeyboard();
        hourPicker.setInputFieldState("hidden");
        boolean ticker = true;
        hourPicker.setType("keyboard");
        for (LocalDateTime availableSlot : freeTimeSlots) {
            ViberButton viberButton = new ViberButton((String.format(sendOrderToPOS + "%s", availableSlot)))
                    .setText(String.format(stringUtils.getButtonStandard(), availableSlot.getHour() +":"+availableSlot.getMinute()))
                    .setColumns(6)
                    .setRows(1)
                    .setSilent(true)
                    .setTextSize(ViberButton.TextSize.MEDIUM)
                    .setTextHAlign(ViberButton.TextAlign.MIDDLE);
            if(ticker){
                viberButton.setBgColor(stringUtils.getPrimarilyColor());
                ticker = false;
            }
            else{
                viberButton.setBgColor(stringUtils.getSecondarilyColor());
                ticker = true;
            }
            hourPicker.addButton(viberButton);

        }
        hourPicker.addButton(new ViberButton(navigateToMainMenu)
                .setText(String.format(stringUtils.getButtonStandard(),stringUtils.getMessageReturnToMenu()))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setSilent(true)
                .setBgColor(stringUtils.getPrimarilyColor()));
        return hourPicker;
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
        confirmationKeyboard.addButton(new ViberButton(navigateToCartMenu)
                .setText(stringUtils.getButtonNo())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setSilent(true)
                .setColumns(3)
                .setRows(2));
        return confirmationKeyboard;
    }

    public void setMainMenu() {
        mainMenu = new ViberKeyboard()
                .setInputFieldState("hidden")
                .setType("keyboard")
                .addButton(new ViberButton(startReservationProcess)
                        .setText("Rezerviši")
                        .setTextSize(ViberButton.TextSize.LARGE)
                        .setBgColor(stringUtils.getPrimarilyColor())
                        .setColumns(2)
                        .setSilent(true)
                        .setRows(2))
                .addButton(new ViberButton(startReservationProcess)
                        .setText("English")
                        .setTextSize(ViberButton.TextSize.LARGE)
                        .setBgColor(stringUtils.getPrimarilyColor())
                        .setColumns(2)
                        .setSilent(true)
                        .setRows(2))
                .addButton(new ViberButton(startReservationProcess)
                        .setText("O nama")
                        .setTextSize(ViberButton.TextSize.LARGE)
                        .setBgColor(stringUtils.getPrimarilyColor())
                        .setColumns(2)
                        .setSilent(true)
                        .setRows(2))
                .addButton(new ViberButton(startReservationProcess)
                        .setText("Istorija rezervacija")
                        .setTextSize(ViberButton.TextSize.LARGE)
                        .setBgColor(stringUtils.getPrimarilyColor())
                        .setColumns(2)
                        .setSilent(true)
                        .setRows(2));
        logger.info("Main Menu has been successfully generated!");
    }
}
