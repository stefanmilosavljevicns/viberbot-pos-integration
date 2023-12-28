package payten.bot.util;

public class BotConstants {
    //KEY NAMES FOR MAPPING ACTION STATES FOR VIBER
    public static final String MESSAGE_EVENT = "message";
    public static final String START_MSG_EVENT = "conversation_started";
    public static final String startReservationProcess = "FNH";
    public static final String aboutUs = "ABT";

    public static final String agreeWithCart = "AGR";
    public static final String selectDayReservation = "DAY";
    public static final String selectDeliveryTime = "TIM";
    public static final String selectCategoryFromMainMenu = "LST";
    public static final String navigateToMainMenu = "BCK";
    public static final String ignoreUserInput = "IGR";

    public static final String sendOrderToPOS = "POS";
    public static final String httpLogFormat = "||Calling endpoint: %s ||Response status: %s ||Response body: %s ||";
    public static final String controlerLogFormat = "||Action: %s ||UserID: %s ||" ;
    public static final String domain = "https://sputnik-it.rs";

    public static final String getCart = "/getCart";
    public static final String getOrdersWithin24Hourse = "/getAllActiveDates";
    public static final String checkIfUserCanOrder = "/checkIfUserCanOrder";
    public static final String getTotalTime = "/getTotalTime";
    public static final String convertToOrderModel = "/convertToOrderModel";
    public static final String clearCart = "/clearCart";
    public static final String updateStartTime = "/updateStartTime";
    public static final String checkTimeSlotAvailability = "/checkTimeSlotAvailability";
    public static final String checkFreeTimeSlots = "/checkFreeTimeSlots";
    public static final String addOrder = "/addOrder";

}
