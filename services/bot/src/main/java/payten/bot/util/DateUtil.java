package payten.bot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

@Component

public class DateUtil {

    @Autowired
    private LocaleUtil localeUtil;


    public String formatMinutes(int minutes){
        if(minutes == 0){
            return "00";
        }
        else {
            return String.valueOf(minutes);
        }
    }
    public LocalDateTime setEndDate(LocalDateTime startDate,double minutes) {
        return startDate.plusMinutes((long) minutes);
    }
    public String translateDayValue(DayOfWeek dayOfWeek,String locale){
        return switch (dayOfWeek) {
            case MONDAY -> localeUtil.getLocalizedMessage("day.monday",locale);
            case TUESDAY -> localeUtil.getLocalizedMessage("day.tuesday",locale);
            case WEDNESDAY -> localeUtil.getLocalizedMessage("day.wednesday",locale);
            case THURSDAY -> localeUtil.getLocalizedMessage("day.thursday",locale);
            case FRIDAY -> localeUtil.getLocalizedMessage("day.friday",locale);
            case SATURDAY -> localeUtil.getLocalizedMessage("day.saturday",locale);
            case SUNDAY -> localeUtil.getLocalizedMessage("day.sunday",locale);
        };
    }
}
