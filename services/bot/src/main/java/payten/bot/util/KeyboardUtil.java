package payten.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import payten.bot.model.MenuItem;
import com.payten.viberutil.keyboard.ViberButton;
import com.payten.viberutil.keyboard.ViberKeyboard;


import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
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
        logger.info("Delivering user: " + mainMenu.toString());
        return mainMenu;

    }
    public ViberKeyboard setListMenu(String listName) throws JsonProcessingException {
        ArrayList<MenuItem> menuItems = httpUtil.getServiceList(listName);
        ViberKeyboard listMenu = new ViberKeyboard();
        listMenu.setInputFieldState("hidden");
        listMenu.setType("keyboard");
        for (MenuItem menuItem : menuItems) {
            listMenu.addButton(new ViberButton(ignoreUserInput)
                            .setBgColor(whiteColor)
                            .setText(String.format(stringUtils.getButtonDescription(), menuItem.getName(), menuItem.getDescription(), menuItem.getPrice()))
                            .setColumns(4)
                            .setRows(2)
                            .setSilent(true)
                            .setTextSize(ViberButton.TextSize.MEDIUM)
                            .setTextHAlign(ViberButton.TextAlign.LEFT))
                    .addButton(new ViberButton(String.format(addingItemToCart + "%s", menuItem.getName()))
                            .setImage(stringUtils.getIconAddItem())
                            .setText(stringUtils.getButtonAddCartItem())
                            .setSilent(true)
                            .setColumns(2)
                            .setRows(2)
                            .setTextSize(ViberButton.TextSize.MEDIUM)
                            .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                            .setTextVAlign(ViberButton.TextAlign.MIDDLE));
        }
        listMenu.addButton(new ViberButton(navigateToMainMenu)
                    .setText(String.format(stringUtils.getButtonStandard(),stringUtils.getMessageReturnToMenu()))
                    .setTextSize(ViberButton.TextSize.LARGE)
                    .setBgColor(stringUtils.getPrimarilyColor())
                    .setSilent(true));
        logger.info("Delivering user: " + listMenu);
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
        logger.info("Delivering user: " + dayPicker);
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
        logger.info("Delivering user: " + hourPicker);
        return hourPicker;
    }
    public ViberKeyboard setYesNo(){
        confirmationKeyboard = new ViberKeyboard();
        confirmationKeyboard.setInputFieldState("hidden");
        confirmationKeyboard.setType("keyboard");
        confirmationKeyboard.addButton(new ViberButton(startPaymentProcess)
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
        logger.info("Delivering user: " + confirmationKeyboard);
        return confirmationKeyboard;
    }
    public ViberKeyboard setCartList(String viberId) throws JsonProcessingException, URISyntaxException {
        ArrayList<String> currentCart = httpUtil.getCartList(viberId);
        ViberKeyboard cartList = new ViberKeyboard();
        cartList.setInputFieldState("hidden");
        cartList.setType("keyboard");
        for (int i = 0; i < currentCart.size() - 1; i++) {
            cartList.addButton(new ViberButton(ignoreUserInput)
                    .setBgColor(whiteColor)
                    .setText(String.format(stringUtils.getButtonStandard(), currentCart.get(i)))
                    .setColumns(4)
                    .setRows(2)
                    .setSilent(true)
                    .setTextSize(ViberButton.TextSize.MEDIUM)
                    .setTextHAlign(ViberButton.TextAlign.LEFT))
                    .addButton(new ViberButton(String.format(removingItemFromCart+"%s", currentCart.get(i)))
                            .setImage(stringUtils.getIconRemoveItem())
                            .setText(stringUtils.getButtonRemoveCartItem())
                            .setColumns(2)
                            .setRows(2)
                            .setSilent(true)
                            .setTextSize(ViberButton.TextSize.MEDIUM).setSilent(true)
                            .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                            .setTextVAlign(ViberButton.TextAlign.MIDDLE));
        }
        cartList.addButton(new ViberButton(ignoreUserInput)
                .setText(String.format(stringUtils.getButtonPriceFormat(), currentCart.get(currentCart.size() - 1)))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setSilent(true)
                .setTextSize(ViberButton.TextSize.LARGE));
        cartList.addButton(new ViberButton(navigateToMainMenu)
                .setText(String.format(stringUtils.getButtonStandard(),stringUtils.getMessageReturnToMenu()))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setSilent(true)
                .setBgColor(stringUtils.getPrimarilyColor()));

        logger.info("Delivering user: " + cartList);
        return cartList;
    }
    public void setMainMenu() {
        ArrayList<String> categoriesTitle = httpUtil.getCategories();
        maxCategories = categoriesTitle.size();
        mainMenu = new ViberKeyboard();
        mainMenu.setInputFieldState("hidden");
        mainMenu.setType("keyboard");
        for (int i = 0; i < maxCategories; i++) {

            if (i == 2) {
                mainMenu.addButton(new ViberButton(navigateToCartMenu)
                        .setText(stringUtils.getButtonMenuCart())
                        .setImage(stringUtils.getIconCart())
                        .setRows(2)
                        .setColumns(2)
                        .setSilent(true)
                        .setBgColor(stringUtils.getSecondarilyColor())
                        .setTextSize(ViberButton.TextSize.LARGE)
                        .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                        .setTextVAlign(ViberButton.TextAlign.MIDDLE));
            }
            mainMenu.addButton(new ViberButton(String.format(selectCategoryFromMainMenu+"%s", categoriesTitle.get(i)))
                    .setText(String.format(stringUtils.getButtonMenuCategories(), categoriesTitle.get(i)))
                    .setImage(String.format(stringUtils.getIconCategory(), categoriesTitle.get(i)))
                    .setRows(2)
                    .setSilent(true)
                    .setColumns(2)
                    .setBgColor(stringUtils.getPrimarilyColor())
                    .setTextSize(ViberButton.TextSize.LARGE)
                    .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                    .setTextVAlign(ViberButton.TextAlign.MIDDLE));
        }
        mainMenu.addButton(new ViberButton(startReservationProcess).setText(stringUtils.getButtonMenuFinishReservation())
                .setImage(stringUtils.getIconCompleteOrder())
                .setRows(2)
                .setSilent(true)
                .setColumns(2)
                .setBgColor(stringUtils.getSecondarilyColor())
                .setTextSize(ViberButton.TextSize.LARGE)
                .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                .setTextVAlign(ViberButton.TextAlign.MIDDLE));
        logger.info("Main Menu has been successfully generated!");
    }
}
