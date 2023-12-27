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
    @Value("${text.menu-cart}")
    private String buttonMenuCart;
    @Value("${text.menu-categories}")
    private String buttonMenuCategories;
    @Value("${text.menu-finish-reservation}")
    private String buttonMenuFinishReservation;
    @Value("${text.price-format}")
    private String buttonPriceFormat;
    @Value("${text.add-cart-item}")
    private String buttonAddCartItem;
    @Value("${text.remove-cart-item}")
    private String buttonRemoveCartItem;
    @Value("${text.item-description}")
    private String buttonDescription;
    @Value("${icon.reserve}")
    private String iconReserve;
    @Value("${icon.complete-order}")
    private String iconCompleteOrder;
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
    @Value("${message.reservation-succes}")
    private String messageSuccessReservation;
    @Value("${message.reservation-reminder}")
    private String messageReservationReminder;
    @Value("${message.reservation-update}")
    private String messageReservationUpdate;
    @Value("${message.unknown-command}")
    private String messageUnknownCommand;

    public String getMessageUnknownCommand() {return messageUnknownCommand;}

    public String getMessageReservationUpdate() {return messageReservationUpdate;}

    public String getMessageReservationReminder() {return messageReservationReminder;}

    public String getButtonMenuCart() {
        return buttonMenuCart;
    }

    public String getButtonMenuCategories() {
        return buttonMenuCategories;
    }

    public String getButtonMenuFinishReservation() {
        return buttonMenuFinishReservation;
    }

    public String getButtonPriceFormat() {
        return buttonPriceFormat;
    }

    public String getButtonAddCartItem() {
        return buttonAddCartItem;
    }

    public String getButtonDescription() {
        return buttonDescription;
    }

    public String getButtonRemoveCartItem() {
        return buttonRemoveCartItem;
    }

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

    public String getIconCompleteOrder() {
        return iconCompleteOrder;
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
