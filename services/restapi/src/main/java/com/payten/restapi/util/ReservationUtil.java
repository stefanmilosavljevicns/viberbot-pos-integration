package com.payten.restapi.util;

import com.payten.restapi.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class ReservationUtil {
    working-hour-mon-fri-start:07:30
    working-hour-mon-fri-end:17:00
    working-hour-start-saturday=00:00
    working-hour-end-saturday=00:00
    working-hour-start-sunday=00:00
    working-hour-end-sunday=00:00
    @Value("${working-hour-mon-fri-start}")

    private String workWeekStart;
    @Value("${working-hour-mon-fri-start}")

    private String workWeekEnd;
    @Value("${working-hour-start-saturday}")

    private String workSaturdayStart;
    @Value("${working-hour-mon-fri-start}")

    private String workSaturdayEnd;
    private ArrayList<LocalDate> getAvailableDaysForReservation(ArrayList<Order> orderList, Integer durationOfReservaton){
    ArrayList<LocalDate> availableDays = new ArrayList<>();
    for(int i = 0; i < 4;i++){
        LocalDateTime isDayAvailable = LocalDateTime.now().plusDays(i);

        if(i==0){

        }
        for(Order order : orderList){
            if(order.getStartTime().getDayOfYear() == isDayAvailable.getDayOfYear()){
                long minutesToSubtract = Duration.between(order.getStartTime(), order.getEndTime()).toMinutes();
            }
        }
    }
    return availableDays;
    }
    private void getTotalWorkingTimeInMinutes(LocalDateTime checkDay){
        if(checkDay.getDayOfWeek()== DayOfWeek.SATURDAY){

        }
        else if(checkDay.getDayOfWeek() == DayOfWeek.SUNDAY){

        }
        else{

        }

    }
}
