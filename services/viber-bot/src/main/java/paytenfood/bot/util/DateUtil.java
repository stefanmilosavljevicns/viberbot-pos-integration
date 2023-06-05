package paytenfood.bot.util;

import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

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
    public  List<LocalDateTime> getWorkingWeekDates() {
        List<LocalDateTime> workingWeekDates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();
        LocalDate startDate;
        if (currentDayOfWeek == DayOfWeek.SATURDAY || currentDayOfWeek == DayOfWeek.SUNDAY) {
            startDate = today.plusDays(1).with(DayOfWeek.MONDAY);
        } else {
            startDate = today.plusDays(1);
        }
        for (int i = 0; i < 5; i++) {
            LocalDate date = startDate.plusDays(i);
            if (date.getDayOfWeek().getValue() < 6) { // Check if it's a weekday (Monday to Friday)
                LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
                workingWeekDates.add(startOfDay);
            }
        }

        return workingWeekDates;
    }

    public LocalDateTime setEndDate(LocalDateTime startDate,double minutes) {
        return startDate.plusMinutes((long) minutes);
    }
    public String translateDayValue(int value){
        switch (value) {
            case 1:
                return "Ponedeljak";
            case 2:
                return "Utorak";
            case 3:
                return "Sreda";
            case 4:
                return "Četvrtak";
            case 5:
                return "Petak";
            default:
                return "Greška";
        }
    }
}
