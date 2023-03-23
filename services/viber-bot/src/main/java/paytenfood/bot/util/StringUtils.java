package paytenfood.bot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringUtils {
    //DEFAULT MESSAGES FOR VIBER BOT
    public static final String ERROR_CART = "Molim Vas da prvo izaberete bar jednu uslugu.";
    public static final String RETURN_MENU = "Povratak na glavni meni";
    public static final String WELCOME_MESSAGE = "Dobrodošli u Viber bot Dental Care ordinacije! Možete zakazati naše usluge praćenjem upustva na ekranu.";
    public static final String CHECK_PAYMENT = "Da li zelite da uplatite rezervaciju online?.";
    public static final String CHECK_CART = "Prikazujem listu izabranih usluga, ako se slažete sa listom pritisnite DA, ukoliko želite da izmenite listu pritisnite NE: \n";
    public static final String CHECK_TIME = "Izaberite vreme rezervacije u formatu:\nDAN.MESEC/SAT:MIn\nPRIMER: 22.03/14:30";
    public static final String ERROR_TIME = "Pogrešan format vremena. Molim da izaberete vreme u formatu: \nDAN.MESEC/SAT:MIn\nPRIMER: 22.03/14:30\n Pokrenite proces naručivaja ponovo. ";

    //ENDPOINTS FROM INTERNAL REST
    public static final String urlMenu = "http://rest:9097/api/v1/getallCategories";
    public static final String urlItems = "http://rest:9097/api/v1/getCategoryItems/";
    public static final String addItems = "http://rest:9097/api/v1/addListItem";
    public static final String rmvItems = "http://rest:9097/api/v1/removeListItem";
    public static final String findPrice = "http://rest:9097/api/v1/getPriceByName/";
    public static final String findDuration = "http://rest:9097/api/v1/getDurationByName/";
    public static final String getCart = "http://rest:9097/api/v1/getListByViberId?viberId=";
    public static final String checkCart = "http://rest:9097/api/v1/getActiveOrders?viberId=";
    public static final String checkPayingStatus = "http://rest:9097/api/v1/getIsPayingStatus/";
    public static final String changePayingStatus = "http://rest:9097/api/v1/changePayingStatus";
    public static final String findTotalTime = "http://rest:9097/api/v1/getTotalTime";
    public static final String checkIfTimeIsAvailable = "http://rest:9097/api/v1/checkAvailability";

}
