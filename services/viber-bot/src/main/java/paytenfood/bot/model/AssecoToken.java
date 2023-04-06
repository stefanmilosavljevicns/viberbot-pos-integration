package paytenfood.bot.model;

public class AssecoToken {
    private String action;
    private String merchantUser;
    private String merchantPassword;
    private String merchant;
    private String customer;
    private String sessionType;
    private String merchantPaymentId;
    private Double amount;
    private String currency;
    private String returnUrl;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMerchantUser() {
        return merchantUser;
    }

    public void setMerchantUser(String merchantUser) {
        this.merchantUser = merchantUser;
    }

    public String getMerchantPassword() {
        return merchantPassword;
    }

    public void setMerchantPassword(String merchantPassword) {
        this.merchantPassword = merchantPassword;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getMerchantPaymentId() {
        return merchantPaymentId;
    }

    public void setMerchantPaymentId(String merchantPaymentId) {
        this.merchantPaymentId = merchantPaymentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}
