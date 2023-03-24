package paytenfood.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import paytenfood.bot.model.ListModel;
import ru.multicon.viber4j.keyboard.ViberButton;
import ru.multicon.viber4j.keyboard.ViberKeyboard;

import java.util.ArrayList;

import static paytenfood.bot.util.StringUtils.RETURN_MENU;

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
        for (int i = 0; i < listModels.size(); i++) {
            listMenu.addButton(new ViberButton("IGNORE").setBgColor("#FFFFFF").setText(String.format("<font color=\"#494E67\"><b>%s</b><br><b>OPIS</b>: %s. <br><b>CENA</b>: %s RSD", listModels.get(i).getName(), listModels.get(i).getDescription(), listModels.get(i).getPrice())).setColumns(4).setRows(2).setTextSize(ViberButton.TextSize.MEDIUM).setTextHAlign(ViberButton.TextAlign.LEFT)).addButton(new ViberButton(String.format("ADD%s", listModels.get(i).getName())).setImage("https://sputnik-it.rs/images/check-mark-final.png").setText("<br><br><br><font color=\"#494E67\"><b>REZERVIŠI</b></font>").setSilent("true").setColumns(2).setRows(2).setTextSize(ViberButton.TextSize.MEDIUM).setTextHAlign(ViberButton.TextAlign.MIDDLE).setTextVAlign(ViberButton.TextAlign.MIDDLE));
        }
        listMenu.addButton(new ViberButton("RETURNTOSTART").setText(String.format("<b><font color=\"#494E67\">%s</b>",RETURN_MENU)).setTextSize(ViberButton.TextSize.LARGE).setBgColor("#7eceea"));
        return listMenu;
    }

    public ViberKeyboard setYesNo(){
        confirmationKeyboard = new ViberKeyboard();
        confirmationKeyboard.addButton(new ViberButton("PAYMENT").setText("<b><font color=\"#494E67\">DA</b>").setTextSize(ViberButton.TextSize.LARGE).setBgColor("#7eceea").setColumns(3).setRows(2));
        confirmationKeyboard.addButton(new ViberButton("CART").setText("<b><font color=\"#494E67\">NE</b>").setTextSize(ViberButton.TextSize.LARGE).setBgColor("#a8aaba").setColumns(3).setRows(2));
        return confirmationKeyboard;
    }

    public ViberKeyboard setPaymentOption(){
        confirmationKeyboard = new ViberKeyboard();
        confirmationKeyboard.addButton(new ViberButton("TIME").setText("<b><font color=\"#494E67\">DA</b>").setTextSize(ViberButton.TextSize.LARGE).setBgColor("#7eceea").setColumns(3).setRows(2));
        confirmationKeyboard.addButton(new ViberButton("TIME").setText("<b><font color=\"#494E67\">NE</b>").setTextSize(ViberButton.TextSize.LARGE).setBgColor("#a8aaba").setColumns(3).setRows(2));
        return confirmationKeyboard;
    }
    public ViberKeyboard setCartList(String viberId) throws JsonProcessingException {

        ArrayList<String> currentCart = httpUtil.getCartList(viberId);

        ViberKeyboard cartList = new ViberKeyboard();
        for (int i = 0; i < currentCart.size() - 1; i++) {
            cartList.addButton(new ViberButton("IGNORE").setBgColor("#FFFFFF").setText(String.format("<b><font color=\"#494E67\">%s</b>", currentCart.get(i))).setColumns(4).setRows(2).setTextSize(ViberButton.TextSize.MEDIUM).setTextHAlign(ViberButton.TextAlign.LEFT)).addButton(new ViberButton(String.format("RMV%s", currentCart.get(i))).setImage("https://sputnik-it.rs/images/remove-mark-final.png").setText("<br><br><font color=\"#494E67\"><b>UKLONI</b></font>").setColumns(2).setRows(2).setTextSize(ViberButton.TextSize.MEDIUM).setSilent("true").setTextHAlign(ViberButton.TextAlign.MIDDLE).setTextVAlign(ViberButton.TextAlign.MIDDLE));
        }
        cartList.addButton(new ViberButton("IGNORE").setText(String.format("<b><font color=\"#494E67\">%s RSD</b>", currentCart.get(currentCart.size() - 1))).setTextSize(ViberButton.TextSize.LARGE).setBgColor("#a8aaba").setTextSize(ViberButton.TextSize.LARGE));
        cartList.addButton(new ViberButton("RETURNTOSTART").setText(String.format("<b><font color=\"#494E67\">%s</b>",RETURN_MENU)).setTextSize(ViberButton.TextSize.LARGE).setBgColor("#7eceea"));
        return cartList;
    }

    //Generating Main Menu this is once per lifecycle call (at beginning of service) where we are loading singleton mainMenu with components
    public void setMainMenu() {
        ArrayList<String> categoriesTitle = httpUtil.getCategories();
        maxCategories = categoriesTitle.size();
        mainMenu = new ViberKeyboard();
        for (int i = 0; i < maxCategories; i++) {

            if (i == 2) {
                mainMenu.addButton(new ViberButton("CART").setText("<br><font color=\"#494E67\"><b>Izabrane usluge</b></font>").setImage("https://sputnik-it.rs/images/Izabrane usluge.png").setRows(2).setColumns(2).setBgColor("#a8aaba").setTextSize(ViberButton.TextSize.LARGE).setTextHAlign(ViberButton.TextAlign.MIDDLE).setTextVAlign(ViberButton.TextAlign.MIDDLE));
            }
            mainMenu.addButton(new ViberButton(String.format("LST%s", categoriesTitle.get(i))).setText(String.format("<br><font color=\"#494E67\"><b>%s</b></font>", categoriesTitle.get(i))).setImage(String.format("https://sputnik-it.rs/images/%s.png", categoriesTitle.get(i))).setRows(2).setColumns(2).setBgColor("#7eceea").setTextSize(ViberButton.TextSize.LARGE).setTextHAlign(ViberButton.TextAlign.MIDDLE).setTextVAlign(ViberButton.TextAlign.MIDDLE));
        }
        mainMenu.addButton(new ViberButton("FINISH").setText("<br><font color=\"#494E67\"><b>Završi rezervaciju</b></font>").setImage("https://sputnik-it.rs/images/Zavrsi rezervaciju.png").setRows(2).setColumns(2).setBgColor("#a8aaba").setTextSize(ViberButton.TextSize.LARGE).setTextHAlign(ViberButton.TextAlign.MIDDLE).setTextVAlign(ViberButton.TextAlign.MIDDLE));
        logger.info("Main Menu has been successfully generated!");
    }
}
