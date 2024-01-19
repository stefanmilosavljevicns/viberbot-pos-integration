package payten.bot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocaleUtil {
    @Autowired
    private MessageSource messageSource;

    public Locale getLocaleFromString(String localeString) {
        switch (localeString) {
            case "ENG":
                return Locale.ENGLISH;
            case "RUS":
                return new Locale("ru", "RU");
            case "SRB":
                return new Locale("sr", "RS");
            default:
                return Locale.ENGLISH; // Default Locale
        }
    }

    public String getLocalizedMessage(String code, String localeString) {
        Locale locale = getLocaleFromString(localeString);
        return messageSource.getMessage(code, null, locale);
    }
}
