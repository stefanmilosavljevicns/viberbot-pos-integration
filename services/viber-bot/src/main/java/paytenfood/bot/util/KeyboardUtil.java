package paytenfood.bot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.multicon.viber4j.keyboard.ViberButton;
import ru.multicon.viber4j.keyboard.ViberKeyboard;
import paytenfood.bot.model.ListModel;

import java.util.ArrayList;

@Component
public class KeyboardUtil {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardUtil.class);
    @Autowired
    private HttpUtil httpUtil;
    int maxCategories;
    private ArrayList<String> categoriesTitle;
    private ArrayList<ListModel> listModels;
    private ViberKeyboard mainMenu;
    private ViberKeyboard listMenu;    
    public ViberKeyboard getMainMenu(){
        return mainMenu;
    }
    public ViberKeyboard setListMenu(String listName) throws JsonProcessingException {
        listModels = httpUtil.getList(listName);
        logger.info(String.format("List name %s",listName));
        listMenu = new ViberKeyboard();
        for (int i = 0; i<listModels.size();i++){
            listMenu.addButton(
                            new ViberButton("0").
                                    setText(String.format("<br><b>%s</b><br>OPIS: %s. <br>CENA: %s",listModels.get(i).getName(),listModels.get(i).getDescription(),listModels.get(i).getPrice())).setColumns(4).setRows(2).setTextSize(ViberButton.TextSize.MEDIUM).setTextHAlign(ViberButton.TextAlign.LEFT)).
                    addButton(
                            new ViberButton(String.format("ADD%s",listModels.get(i))).
                                    setImage("https://sputnik-it.rs/images/check-mark-final.png").setText("<br><br><br><font color=\"#494E67\"><b>REZERVIŠI</b></font>").setColumns(2).setRows(2).setTextSize(ViberButton.TextSize.MEDIUM).setTextHAlign(ViberButton.TextAlign.MIDDLE).setTextVAlign(ViberButton.TextAlign.MIDDLE));

        }
        listMenu.addButton(
                new ViberButton("0").
                        setText("<b>POVRATAK NA GLAVNI MENI</b>").setTextSize(ViberButton.TextSize.LARGE).setBgColor("#7eceea")
        );
        return listMenu;
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
