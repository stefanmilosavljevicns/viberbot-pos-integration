package paytenfood.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import paytenfood.bot.model.ListModel;
import ru.multicon.viber4j.keyboard.ButtonContainer;
import ru.multicon.viber4j.keyboard.ViberButton;
import ru.multicon.viber4j.keyboard.ViberKeyboard;


import java.util.ArrayList;

import static paytenfood.bot.util.StringUtils.*;

@Component
public class KeyboardUtil {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardUtil.class);
    int maxCategories;
    @Autowired
    private HttpUtil httpUtil;
    private ViberKeyboard mainMenu;
    private ViberKeyboard confirmationKeyboard;

    public ViberKeyboard getMainMenu() {
        return mainMenu;
    }

    public ViberKeyboard setListMenu(String listName) throws JsonProcessingException {
        ArrayList<ListModel> listModels = httpUtil.getServiceList(listName);
        ViberKeyboard listMenu = new ViberKeyboard();
        listMenu.setInputFieldState("hidden");
        listMenu.setType("keyboard");
        for (int i = 0; i < listModels.size(); i++) {
            listMenu.addButton(new ViberButton(ignoreUserInput)
                    .setBgColor(whiteColor)
                    .setText(String.format("<font color=\"#494E67\"><b>%s</b><br><b>OPIS</b>: %s. <br><b>CENA</b>: %s RSD", listModels.get(i).getName(), listModels.get(i).getDescription(), listModels.get(i).getPrice()))
                    .setColumns(4)
                    .setRows(2)
                    .setSilent(true)
                    .setTextSize(ViberButton.TextSize.MEDIUM)
                    .setTextHAlign(ViberButton.TextAlign.LEFT))
                    .addButton(new ViberButton(String.format(addingItemToCart+"%s", listModels.get(i).getName()))
                            .setImage(reserveItemIcon)
                            .setText("<br><br><br><font color=\"#494E67\"><b>REZERVIŠI</b></font>")
                            .setSilent(true)
                            .setColumns(2)
                            .setRows(2)
                            .setTextSize(ViberButton.TextSize.MEDIUM)
                            .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                            .setTextVAlign(ViberButton.TextAlign.MIDDLE));
        }
        listMenu.addButton(new ViberButton(navigateToMainMenu)
                    .setText(String.format(standardTextFormat,RETURN_MENU))
                    .setTextSize(ViberButton.TextSize.LARGE)
                    .setBgColor(primarilyColor)
                    .setSilent(true));
        return listMenu;
    }

    public ViberKeyboard setYesNo(){
        confirmationKeyboard = new ViberKeyboard();
        confirmationKeyboard.setInputFieldState("hidden");
        confirmationKeyboard.setType("keyboard");
        confirmationKeyboard.addButton(new ViberButton(startPaymentProcess)
                .setText(yesButtonFormat)
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(primarilyColor)
                .setColumns(3)
                .setSilent(true)
                .setRows(2));
        confirmationKeyboard.addButton(new ViberButton(navigateToCartMenu)
                .setText(noButtonFormat)
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(secondarilyColor)
                .setSilent(true)
                .setColumns(3)
                .setRows(2));
        return confirmationKeyboard;
    }

    public ViberKeyboard setPaymentOption(){
        confirmationKeyboard = new ViberKeyboard();
        confirmationKeyboard.setInputFieldState("hidden");
        confirmationKeyboard.setType("keyboard");
        confirmationKeyboard.addButton(new ViberButton(selectDeliveryTime)
                .setText(yesButtonFormat)
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(primarilyColor)
                .setColumns(3)
                .setSilent(true)
                .setRows(2));
        confirmationKeyboard.addButton(new ViberButton(selectDeliveryTime)
                .setText(noButtonFormat)
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(secondarilyColor)
                .setSilent(true)
                .setColumns(3)
                .setRows(2));
        return confirmationKeyboard;
    }

    public ViberKeyboard setCartList(String viberId) throws JsonProcessingException {

        ArrayList<String> currentCart = httpUtil.getCartList(viberId);

        ViberKeyboard cartList = new ViberKeyboard();
        cartList.setInputFieldState("hidden");
        cartList.setType("keyboard");
        for (int i = 0; i < currentCart.size() - 1; i++) {
            cartList.addButton(new ViberButton(ignoreUserInput)
                    .setBgColor(whiteColor)
                    .setText(String.format(standardTextFormat, currentCart.get(i)))
                    .setColumns(4)
                    .setRows(2)
                    .setSilent(true)
                    .setTextSize(ViberButton.TextSize.MEDIUM)
                    .setTextHAlign(ViberButton.TextAlign.LEFT))
                    .addButton(new ViberButton(String.format(removingItemFromCart+"%s", currentCart.get(i)))
                            .setImage(removeItemIcon)
                            .setText("<br><br><font color=\"#494E67\"><b>UKLONI</b></font>")
                            .setColumns(2)
                            .setRows(2)
                            .setSilent(true)
                            .setTextSize(ViberButton.TextSize.MEDIUM).setSilent(true)
                            .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                            .setTextVAlign(ViberButton.TextAlign.MIDDLE));
        }
        cartList.addButton(new ViberButton(ignoreUserInput)
                .setText(String.format("<b><font color=\"#494E67\">%s RSD</b>", currentCart.get(currentCart.size() - 1)))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setBgColor(secondarilyColor)
                .setSilent(true)
                .setTextSize(ViberButton.TextSize.LARGE));
        cartList.addButton(new ViberButton(navigateToMainMenu)
                .setText(String.format(standardTextFormat,RETURN_MENU))
                .setTextSize(ViberButton.TextSize.LARGE)
                .setSilent(true)
                .setBgColor(primarilyColor));
        return cartList;
    }

    //Generating Main Menu this is once per lifecycle call (at beginning of service) where we are loading singleton mainMenu with components
    public void setMainMenu() {
        ArrayList<String> categoriesTitle = httpUtil.getCategories();
        maxCategories = categoriesTitle.size();
        mainMenu = new ViberKeyboard();
        mainMenu.setInputFieldState("hidden");
        mainMenu.setType("keyboard");
        for (int i = 0; i < maxCategories; i++) {

            if (i == 2) {
                mainMenu.addButton(new ViberButton(navigateToCartMenu)
                        .setText("<br><font color=\"#494E67\"><b>Izabrane usluge</b></font>")
                        .setImage(cartMenuIcon)
                        .setRows(2)
                        .setColumns(2)
                        .setSilent(true)
                        .setBgColor(secondarilyColor)
                        .setTextSize(ViberButton.TextSize.LARGE)
                        .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                        .setTextVAlign(ViberButton.TextAlign.MIDDLE));
            }
            mainMenu.addButton(new ViberButton(String.format(selectCategoryFromMainMenu+"%s", categoriesTitle.get(i)))
                    .setText(String.format("<br><font color=\"#494E67\"><b>%s</b></font>", categoriesTitle.get(i)))
                    .setImage(String.format(categoryMenuIcon, categoriesTitle.get(i)))
                    .setRows(2)
                    .setSilent(true)
                    .setColumns(2)
                    .setBgColor(primarilyColor)
                    .setTextSize(ViberButton.TextSize.LARGE)
                    .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                    .setTextVAlign(ViberButton.TextAlign.MIDDLE));
        }
        mainMenu.addButton(new ViberButton(startFinishProcess).setText("<br><font color=\"#494E67\"><b>Završi rezervaciju</b></font>")
                .setImage(finishOrderMenuIcon)
                .setRows(2)
                .setSilent(true)
                .setColumns(2)
                .setBgColor(secondarilyColor)
                .setTextSize(ViberButton.TextSize.LARGE)
                .setTextHAlign(ViberButton.TextAlign.MIDDLE)
                .setTextVAlign(ViberButton.TextAlign.MIDDLE));
        logger.info("Main Menu has been successfully generated!");
    }
}
