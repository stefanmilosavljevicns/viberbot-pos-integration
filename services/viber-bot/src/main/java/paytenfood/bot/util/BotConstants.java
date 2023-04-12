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

    //COLORS AND TEXT STYLING FOR VIBER BOT
    public static final String textColor = "<font color=\"#494E67\">";
    public static final String primarilyColor = "#7eceea";
    public static final String secondarilyColor = "#a8aaba";
    public static final String whiteColor = "#FFFFFF";
    public static final String yesButtonFormat = "<b><font color=\"#494E67\">DA</b>";
    public static final String noButtonFormat = "<b><font color=\"#494E67\">NE</b>";
    public static final String standardTextFormat = "<b><font color=\"#494E67\">%s</b>";
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
    public static final String rootPath = "http://rest:9097/api/v1";
    public static final String urlMenu = "http://rest:9097/api/v1/getallCategories";
    public static final String urlItems = "http://rest:9097/api/v1/getCategoryItems/";
    public static final String addItems = "http://rest:9097/api/v1/addCartItem";
    public static final String rmvItems = "http://rest:9097/api/v1/removeCartItem";
    public static final String getCart = "http://rest:9097/api/v1/getCart";
    public static final String checkCart = "http://rest:9097/api/v1/checkIfCartIsEmpty";
    public static final String checkPayingStatus = "http://rest:9097/api/v1/checkPayingStatus";
    public static final String changePayingStatus = "http://rest:9097/api/v1/changePayingStatus";
    public static final String findTotalTime = "http://rest:9097/api/v1/getTotalTime";
    public static final String findTotalPrice = "http://rest:9097/api/v1/getTotalPrice";
    public static final String getCurrentList = "http://rest:9097/api/v1/convertToOrderModel";
    public static final String findItem = "http://rest:9097/api/v1/getMenuItemByName/";
    public static final String completeOrder = "http://rest:9097/api/v1/clearCart";
    public static final String checkIfTimeIsAvailable = "http://rest:9097/api/v1/checkTimeSlotAvailability";
    public static final String sendOrder = "http://rest:9097/api/v1/addOrder";
    public static final String assecoGetCurrentCart = "http://rest:9097/api/v1/assecoOrderConverter";
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
