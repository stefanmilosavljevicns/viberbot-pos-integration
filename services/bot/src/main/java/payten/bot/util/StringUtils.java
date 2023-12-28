package payten.bot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StringUtils {
    @Value("${viber.token}")
    private String botToken;
    @Value("${viber.bot-path}")
    private String botPath;
    @Value("${rest.address}")
    private String restAdress;
    @Value("${color.primarily}")
    private String primarilyColor;
    @Value("${color.secondarily}")
    private String secondarilyColor;
    @Value("${text.yes-button}")
    private String buttonYes;
    @Value("${text.no-button}")
    private String buttonNo;
    @Value("${text.standard-button}")
    private String buttonStandard;
    @Value("${icon.reserve}")
    private String iconReserve;
    @Value("${icon.about-us}")
    private String iconAboutUs;
    @Value("${icon.previous-orders}")
    private String previousOrders;
    @Value("${icon.choose-language}")
    private String iconChooseLanguage;
    @Value("${message.error}")
    private String messageError;
    @Value("${message.error-time}")
    private String messageErrorTime;
    @Value("${message.welcome}")
    private String messageWelcome;
    @Value("${message.return-to-menu}")
    private String messageReturnToMenu;
    @Value("${message.check-cart}")
    private String messageCheckCart;
    @Value("${message.check-time}")
    private String messageCheckTime;
    @Value("${message.about-us}")
    private String messageAboutUs;
    @Value("${message.reservation-succes}")
    private String messageSuccessReservation;
    @Value("${message.reservation-reminder}")
    private String messageReservationReminder;
    @Value("${message.reservation-update}")
    private String messageReservationUpdate;
    @Value("${message.unknown-command}")
    private String messageUnknownCommand;
    @Value("${image-about-us-1}")
    private String imageAboutUs1;
    @Value("${image-about-us-2}")
    private String imageAboutUs2;

    public String getImageAboutUs1() {
        return imageAboutUs1;
    }

    public String getImageAboutUs2() {
        return imageAboutUs2;
    }

    public String getMessageAboutUs() {return messageAboutUs;}
    public String getMessageUnknownCommand() {return messageUnknownCommand;}

    public String getMessageReservationUpdate() {return messageReservationUpdate;}

    public String getMessageReservationReminder() {return messageReservationReminder;}


    public String getMessageError() {
        return messageError;
    }

    public String getMessageErrorTime() {
        return messageErrorTime;
    }

    public String getMessageWelcome() {
        return messageWelcome;
    }

    public String getMessageReturnToMenu() {
        return messageReturnToMenu;
    }

    public String getMessageCheckCart() {
        return messageCheckCart;
    }

    public String getMessageCheckTime() {
        return messageCheckTime;
    }

    public String getMessageSuccessReservation() {
        return messageSuccessReservation;
    }

    public String getIconReserve() {
        return iconReserve;
    }

    public String getIconAboutUs() {
        return iconAboutUs;
    }

    public String getIconPreviousOrders() {
        return previousOrders;
    }

    public String getIconChooseLanguage() {
        return iconChooseLanguage;
    }

    public String getButtonStandard() {
        return buttonStandard;
    }

    public String getButtonYes() {
        return buttonYes;
    }

    public String getButtonNo() {
        return buttonNo;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getBotPath() {
        return botPath;
    }

    public String getPrimarilyColor() {
        return primarilyColor;
    }

    public String getSecondarilyColor() {
        return secondarilyColor;
    }

    public String getRestAdress() {
        return restAdress;
    }




}
