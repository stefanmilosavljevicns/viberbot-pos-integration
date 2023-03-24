package paytenfood.bot.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component

public class DateUtil {

    //Used for converting user input of time in format 14.02/15:53 into localdatetime
    public LocalDateTime parseUserInput(String userInput) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.dd.MM HH:mm");
        try {
            return LocalDateTime.parse( Year.now().getValue() + "." + userInput, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    public LocalDateTime setEndDate(LocalDateTime startDate,double minutes) {
        return startDate.plusMinutes((long) minutes);
    }
}
