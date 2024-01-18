package payten.bot.util;

public class BotConstants {
    //KEY NAMES FOR MAPPING ACTION STATES FOR VIBER
    public static final String MESSAGE_EVENT = "message";
    public static final String START_MSG_EVENT = "conversation_started";
    public static final String aboutUs = "ABT";
    public static final String agreeWithCart = "AGR";
    public static final String selectDayReservation = "DAY";
    public static final String navigateToMainMenu = "BCK";
    public static final String ignoreUserInput = "IGR";
    public static final String changeLanguageMenu = "LOC";
    public static final String changeUserLocaleSrb = "SRB";
    public static final String changeUserLocaleEng = "ENG";
    public static final String changeUserLocaleRus = "RUS";

    public static final String historyOfReservation = "HST";
    public static final String listAvailableTimeSlot = "LST";
    public static final String selectDurationOfReservation = "DRT";

    public static final String sendOrderToPOS = "POS";
    public static final String httpLogFormat = "||Calling endpoint: %s ||Response status: %s ||Response body: %s ||";
    public static final String controlerLogFormat = "||Action: %s ||UserID: %s ||" ;
    public static final String domain = "https://mestozakafu.com";

    public static final String getOrdersWithin24Hourse = "/getAllActiveDates";
    public static final String checkIfUserCanOrder = "/checkIfUserCanOrder";
    public static final String listAvailableTimeSlots = "/findAvailableTimeSlotsForReservation";
    public static final String changeLocale = "/changeLocale";
    public static final String userLocale = "/getUserLocale";
    public static final String getTotalTime = "/getTotalTime";
    public static final String getHistoryOfReservations = "/historyOfReservation";
    public static final String updateStartTime = "/updateStartTime";
    public static final String checkTimeSlotAvailability = "/checkTimeSlotAvailability";
    public static final String checkFreeTimeSlots = "/checkFreeTimeSlots";
    public static final String addOrder = "/addOrder";

    public static final String findAvailableDays = "/findAvailableDays";



}
