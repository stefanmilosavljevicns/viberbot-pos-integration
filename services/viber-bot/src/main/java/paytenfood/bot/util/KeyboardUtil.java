package paytenfood.bot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.multicon.viber4j.keyboard.ViberButton;
import ru.multicon.viber4j.keyboard.ViberKeyboard;

import java.util.ArrayList;

@Component
public class KeyboardUtil {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardUtil.class);
    @Autowired
    private HttpUtil httpUtil;
    int maxCategories;
    private ArrayList<String> categoriesTitle;
    private ViberKeyboard mainMenu;

    public ViberKeyboard getMainMenu(){
        return mainMenu;
    }
    public void setMainMenu(){
        categoriesTitle = httpUtil.getCategories();
        maxCategories = categoriesTitle.size();
        mainMenu = new ViberKeyboard();
        for (int i = 0; i<maxCategories;i++){

            if(i == 2){
                mainMenu.addButton(
                        new ViberButton("1").
                                setText("<br><font color=\"#494E67\"><b>Izabrane usluge</b></font>").setImage("https://sputnik-it.rs/images/Izabrane usluge.png").setRows(2).setColumns(2).setBgColor("#a8aaba").setTextSize(ViberButton.TextSize.LARGE).setTextHAlign(ViberButton.TextAlign.MIDDLE).setTextVAlign(ViberButton.TextAlign.MIDDLE));
            }
                mainMenu.addButton(
                        new ViberButton(String.format("LIST%s",categoriesTitle.get(i))).
                                setText(String.format("<br><font color=\"#494E67\"><b>%s</b></font>",categoriesTitle.get(i))).setImage(String.format("https://sputnik-it.rs/images/%s.png",categoriesTitle.get(i))).setRows(2).setColumns(2).setBgColor("#7eceea").setTextSize(ViberButton.TextSize.LARGE).setTextHAlign(ViberButton.TextAlign.MIDDLE).setTextVAlign(ViberButton.TextAlign.MIDDLE));
                ;
        }
        mainMenu.addButton(
                new ViberButton("1").
                        setText("<br><font color=\"#494E67\"><b>Zavrsi rezervaciju</b></font>").setImage("https://sputnik-it.rs/images/Zavrsi rezervaciju.png").setRows(2).setColumns(2).setBgColor("#a8aaba").setTextSize(ViberButton.TextSize.LARGE).setTextHAlign(ViberButton.TextAlign.MIDDLE).setTextVAlign(ViberButton.TextAlign.MIDDLE));
        logger.info("Main Menu has been successfully generated!");
    }
}
