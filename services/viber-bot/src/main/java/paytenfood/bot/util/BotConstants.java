package paytenfood.bot.util;

public class BotConstants {
    //KEY NAMES FOR MAPPING ACTION STATES FOR VIBER
    public static final String MESSAGE_EVENT = "message";
    public static final String START_MSG_EVENT = "conversation_started";
    public static final String navigateToCartMenu = "CRT";
    public static final String startReservationProcess = "FNH";
    public static final String clearCartAndFinishSession = "CLR";
    public static final String startPaymentProcess = "PYT";
    public static final String selectDayReservation = "DAY";
    public static final String selectDeliveryTime = "TIM";
    public static final String selectCategoryFromMainMenu = "LST";
    public static final String navigateToMainMenu = "RTS";
    public static final String ignoreUserInput = "IGR";
    public static final String addingItemToCart = "ADD";
    public static final String removingItemFromCart = "RMV";
    //LOG FORMAT
    public static final String httpLogFormat = "||Calling endpoint: %s ||Response status: %s ||Response body: %s";

    public static final String whiteColor = "#FFFFFF";
    //DOMAIN
    public static final String domain = "https://sputnik-it.rs";

    //ENDPOINTS FROM INTERNAL REST
    public static final String getAllCategories = "/getallCategories";
    public static final String getCategoryItems = "/getCategoryItems/";
    public static final String addItemToCart = "/addItemToCart";
    public static final String removeItemFromCart = "/removeItemFromCart";
    public static final String getCart = "/getCart";
    public static final String getOrdersWithin24Hourse = "/getAllActiveDates";
    public static final String checkIfCartIsEmpty = "/checkIfCartIsEmpty";
    public static final String checkPayingStatus = "/checkPayingStatus";
    public static final String changePayingStatus = "/changePayingStatus";
    public static final String getTotalTime = "/getTotalTime";
    public static final String getTotalPrice = "/getTotalPrice";
    public static final String convertToOrderModel = "/convertToOrderModel";
    public static final String getItemByName = "/getItemByName/";
    public static final String clearCart = "/clearCart";
    public static final String updateStartTime = "/updateStartTime";
    public static final String checkTimeSlotAvailability = "/checkTimeSlotAvailability";
    public static final String addOrder = "/addOrder";
    public static final String assecoOrderConverter = "/assecoOrderConverter";
    public static final String assecoPayingOnline = "https://entegrasyon.asseco-see.com.tr/msu/api/v2";
    public static final String assecoPaymentPage = "https://entegrasyon.asseco-see.com.tr/chipcard/pay3d/";

    public static final String redirection = "https://sputnik-it.rs/paymentinfo";

    //OTHER MESSAGES FOR VIBER
    public static final String returnToPayment = "Povratak na odabir plaćanja";
    public static final String failedPayment = "Da li želite da ponovite online plaćanje?";


}
