package com.payten.restapi.util;

import com.payten.restapi.controller.OrderController;
import com.payten.restapi.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class ReservationUtil {

    @Value("${working-hour-mon-fri-start}")

    private String workWeekStart;
    @Value("${working-hour-mon-fri-start}")

    private String workWeekEnd;
    @Value("${working-hour-start-saturday}")

    private String workSaturdayStart;
    @Value("${working-hour-end-saturday}")

    private String workSaturdayEnd;
    @Value("${working-hour-start-sunday}")

    private String workSundayStart;
    @Value("${working-hour-end-sunday}")

    private String workSundayEnd;
    @Value("${number-of-tables}")

    private int numberOfTables;
    private static final Logger logger = LoggerFactory.getLogger(ReservationUtil.class);

    public ArrayList<LocalDate> getAvailableDaysForReservation(ArrayList<Order> orderList, Integer durationOfReservaton){
    ArrayList<LocalDate> availableDays = new ArrayList<>();
    for(int i = 0; i < 4;i++){
        int totalWorkingMinutes = 0;
        logger.info("TREBA DA BUDE 0"+String.valueOf(totalWorkingMinutes));
        LocalDateTime isDayAvailable = LocalDateTime.now().plusDays(i);
        if(i==0){
            int currentMinutes = isDayAvailable.getHour() * 60 + isDayAvailable.getMinute();
            totalWorkingMinutes = getTotalWorkingTimeInMinutesForCurrentDay(isDayAvailable,currentMinutes) * numberOfTables;
        }
        else{
            totalWorkingMinutes = getTotalWorkingTimeInMinutes(isDayAvailable) * numberOfTables;
        }
        logger.info("VREDNOST PRE ODUZIMANJA"+String.valueOf(totalWorkingMinutes));
        if(totalWorkingMinutes <= 0){
            continue;
        }
        for(Order order : orderList){
            if(order.getStartTime().getDayOfYear() == isDayAvailable.getDayOfYear()){
                long minutesToSubtract = Duration.between(order.getStartTime(), order.getEndTime()).toMinutes();
                totalWorkingMinutes -= (int) minutesToSubtract;
                logger.info("ODUZIMANJE"+String.valueOf(totalWorkingMinutes));
            }
        }
        if(totalWorkingMinutes - durationOfReservaton >= 0){
            availableDays.add(LocalDate.from(isDayAvailable));
        }
    }
    return availableDays;
    }


    private int getTotalWorkingTimeInMinutes(LocalDateTime checkDay){
        if(checkDay.getDayOfWeek()== DayOfWeek.SATURDAY){
            return convertToMinutes(workSaturdayEnd) - convertToMinutes(workSaturdayStart);
        }
        else if(checkDay.getDayOfWeek() == DayOfWeek.SUNDAY){
            return convertToMinutes(workSundayEnd) - convertToMinutes(workSundayStart);
        }
        else{
            return convertToMinutes(workWeekEnd) - convertToMinutes(workWeekStart);
        }

    }

    private int getTotalWorkingTimeInMinutesForCurrentDay(LocalDateTime checkDay,int currentTimeInMinutes){
        if(checkDay.getDayOfWeek()== DayOfWeek.SATURDAY){
            if(currentTimeInMinutes > convertToMinutes(workSaturdayStart)){
                return convertToMinutes(workSaturdayEnd) - currentTimeInMinutes;
            }
            else{
                return convertToMinutes(workSaturdayEnd) - convertToMinutes(workSaturdayStart);
            }
        }
        else if(checkDay.getDayOfWeek() == DayOfWeek.SUNDAY){
            if(currentTimeInMinutes > convertToMinutes(workSundayStart)){
                return convertToMinutes(workSundayEnd) - currentTimeInMinutes;
            }
            else{
                return convertToMinutes(workSundayEnd) - convertToMinutes(workSundayStart);
            }
        }
        else{
            if(currentTimeInMinutes > convertToMinutes(workWeekStart)){
                return convertToMinutes(workWeekEnd) - currentTimeInMinutes;
            }
            else{
                return convertToMinutes(workWeekEnd) - convertToMinutes(workWeekStart);
            }
        }

    }
    public static int convertToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        logger.info("KONVERTER"+hours * 60 + minutes);
        return hours * 60 + minutes;
    }
}
