package paytenfood.bot.util;

public class BotConstants {
    //KEY NAMES FOR MAPPING ACTION STATES FOR VIBER
    public static final String MESSAGE_EVENT = "message";
    public static final String START_MSG_EVENT = "conversation_started";
    public static final String navigateToCartMenu = "CRT";
    public static final String startFinishProcess = "FNH";
    public static final String startPaymentProcess = "PYT";
    public static final String selectDeliveryTime = "TIM";
    public static final String selectCategoryFromMainMenu = "LST";
    public static final String navigateToMainMenu = "RTS";
    public static final String ignoreUserInput = "IGR";
    public static final String addingItemToCart = "ADD";
    public static final String removingItemFromCart = "RMV";
    //LOG FORMAT
    public static final String httpLogFormat = "||Calling endpoint: %s ||Response status: %s ||Response body: %s";
    //COLORS AND TEXT STYLING FOR VIBER BOT
    public static final String textColor = "<font color=\"#494E67\">";
    public static final String primarilyColor = "#7eceea";
    public static final String secondarilyColor = "#a8aaba";
    public static final String whiteColor = "#FFFFFF";
    public static final String yesButtonFormat = "<b><font color=\"#494E67\">DA</b>";
    public static final String noButtonFormat = "<b><font color=\"#494E67\">NE</b>";
    public static final String standardTextFormat = "<b><font color=\"#494E67\">%s</b>";
    //DOMAIN
    public static final String domain = "https://sputnik-it.rs";
    //IMAGES
    public static final String removeItemIcon = "https://sputnik-it.rs/images/remove-mark-final.png";
    public static final String cartMenuIcon = "https://sputnik-it.rs/images/Izabrane usluge.png";
    public static final String finishOrderMenuIcon = "https://sputnik-it.rs/images/Zavrsi rezervaciju.png";
    public static final String categoryMenuIcon = "https://sputnik-it.rs/images/%s.png";
    public static final String reserveItemIcon = "https://sputnik-it.rs/images/check-mark-final.png";
    //DEFAULT MESSAGES FOR VIBER BOT
    public static final String ERROR_CART = "Molim Vas da prvo izaberete bar jednu uslugu.";
    public static final String RETURN_MENU = "Povratak na glavni meni";
    public static final String WELCOME_MESSAGE = "Dobrodošli u Viber bot Dental Care ordinacije! Možete zakazati naše usluge praćenjem uputstva na ekranu.";
    public static final String CHECK_PAYMENT = "Da li zelite da uplatite rezervaciju online?.";
    public static final String CHECK_CART = "Prikazujem listu izabranih usluga, ako se slažete sa listom pritisnite DA, ukoliko želite da izmenite listu pritisnite NE: \n";
    public static final String CHECK_TIME = "Izaberite vreme rezervacije u formatu:\nDAN.MESEC SAT:MIn\nPRIMER: 22.03 14:30";
    public static final String ERROR_TIME = "Pogrešan format vremena. Molim da izaberete vreme u formatu: \nDAN.MESEC SAT:MIn\nPRIMER: 22.03 14:30\n Pokrenite proces naručivaja ponovo. ";
    public static final String SUCCESS_RESERVATION = "Uspešno ste završili rezervaciju!";

    //ENDPOINTS FROM INTERNAL REST
    public static final String getAllCategories = "/getallCategories";
    public static final String getCategoryItems = "/getCategoryItems/";
    public static final String addItemToCart = "/addItemToCart";
    public static final String removeItemFromCart = "/removeItemFromCart";
    public static final String getCart = "/getCart";
    public static final String checkIfCartIsEmpty = "/checkIfCartIsEmpty";
    public static final String checkPayingStatus = "/checkPayingStatus";
    public static final String changePayingStatus = "/changePayingStatus";
    public static final String getTotalTime = "/getTotalTime";
    public static final String getTotalPrice = "/getTotalPrice";
    public static final String convertToOrderModel = "/convertToOrderModel";
    public static final String getItemByName = "/getItemByName/";
    public static final String completeOrder = "/clearCart";
    public static final String checkTimeSlotAvailability = "/checkTimeSlotAvailability";
    public static final String addOrder = "/addOrder";
    public static final String assecoOrderConverter = "/assecoOrderConverter";
    public static final String assecoPayingOnline ="https://entegrasyon.asseco-see.com.tr/msu/api/v2";
    public static final String assecoPaymentPage = "https://entegrasyon.asseco-see.com.tr/chipcard/pay3d/";

    public static final String redirectPaymentMessage = "Redirektujem Vas na stranicu za plaćanje po završenoj transakciji bićete vraćeni u Viber";

    public static final String merchant = "chipcardtest01";
    public static final String merchantUser = "api.test@payten.com";
    public static final String merchantPw = "Hephr=R4SKNycaLf";
    public static final String redirection = "https://sputnik-it.rs/paymentinfo";
    public static final String botPath = "/viberbot";
    public static final String successfulPayment = "Uspešno ste završili online plaćanje.";

}
