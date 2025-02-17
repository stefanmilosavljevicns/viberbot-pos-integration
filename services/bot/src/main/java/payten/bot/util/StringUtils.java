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
    @Value("${icon.cart}")
    private String iconCart;
    @Value("${icon.complete-order}")
    private String iconCompleteOrder;
    @Value("${icon.category}")
    private String iconCategory;
    @Value("${icon.add-item}")
    private String iconAddItem;
    @Value("${icon.remove-item}")
    private String iconRemoveItem;
    @Value("${message.error}")
    private String messageError;
    @Value("${message.error-time}")
    private String messageErrorTime;
    @Value("${message.welcome}")
    private String messageWelcome;
    @Value("${message.return-to-menu}")
    private String messageReturnToMenu;
    @Value("${message.payment-online}")
    private String messagePaymentOnline;
    @Value("${message.check-cart}")
    private String messageCheckCart;
    @Value("${message.check-time}")
    private String messageCheckTime;
    @Value("${message.success-reservation}")
    private String messageSuccessReservation;

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

    public String getMessagePaymentOnline() {
        return messagePaymentOnline;
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

    public String getIconCart() {
        return iconCart;
    }

    public String getIconCompleteOrder() {
        return iconCompleteOrder;
    }

    public String getIconCategory() {
        return iconCategory;
    }

    public String getIconAddItem() {
        return iconAddItem;
    }

    public String getIconRemoveItem() {
        return iconRemoveItem;
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
