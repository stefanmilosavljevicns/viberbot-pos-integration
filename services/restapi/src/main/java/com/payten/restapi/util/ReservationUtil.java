package com.payten.restapi.util;

import com.payten.restapi.controller.OrderController;
import com.payten.restapi.model.DTO.SuggestedReservationSlot;
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
    @Value("${working-hour-mon-fri-end}")

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

    public ArrayList<SuggestedReservationSlot> getAvailableTimeSlotsForReservation(ArrayList<Order> orderList, Integer durationOfReservation,LocalDate targetedDate){
        ArrayList<SuggestedReservationSlot> availableTimeSlots = new ArrayList<>();
        ArrayList<SuggestedReservationSlot> reservedSlots = new ArrayList<>();
        LocalDateTime startWorkingHours;
        LocalDateTime endWorkingHours;
        for(Order order: orderList){
            reservedSlots.add(new SuggestedReservationSlot(order.getStartTime(),order.getEndTime(),order.getTableNumber()));
        }
        //TODO Dodaj ovde logiku da raspozna dane sad znamo da vikend nije radan
        if(targetedDate.getDayOfYear() > LocalDate.now().getDayOfYear()){
            startWorkingHours = targetedDate.atStartOfDay();
            startWorkingHours = startWorkingHours.plusMinutes(convertToMinutes(workWeekStart));
        }
        else{
            startWorkingHours = targetedDate.atStartOfDay();
            startWorkingHours = startWorkingHours.plusMinutes(LocalDateTime.now().getHour() * 60 + 60);
        }
        endWorkingHours = targetedDate.atStartOfDay();
        endWorkingHours = endWorkingHours.plusMinutes(convertToMinutes(workWeekEnd));
        LocalDateTime currentTime = startWorkingHours;
        while (currentTime.plusMinutes(durationOfReservation).isBefore(endWorkingHours) || currentTime.plusMinutes(durationOfReservation).isEqual(endWorkingHours)) {
            boolean slotFound = false;

            for (int table = 1; table <= numberOfTables && !slotFound; table++) {
                LocalDateTime endTime = currentTime.plusMinutes(durationOfReservation);
                if (isSlotAvailable(currentTime, endTime, table, reservedSlots)) {
                    availableTimeSlots.add(new SuggestedReservationSlot(currentTime, endTime, table));
                    slotFound = true; // Stop checking other tables for this time slot
                }
            }
            currentTime = currentTime.plusMinutes(30);
        }

        return availableTimeSlots;
    }
    public ArrayList<LocalDate> getAvailableDaysForReservation(ArrayList<Order> orderList, Integer durationOfReservaton){
    ArrayList<LocalDate> availableDays = new ArrayList<>();
    for(int i = 0; i < 4;i++){
        int totalWorkingMinutes = 0;
        LocalDateTime isDayAvailable = LocalDateTime.now().plusDays(i);
        if(i==0){
            int currentMinutes = isDayAvailable.getHour() * 60 + isDayAvailable.getMinute();
            totalWorkingMinutes = getTotalWorkingTimeInMinutesForCurrentDay(isDayAvailable,currentMinutes,durationOfReservaton) * numberOfTables;
        }
        else{
            totalWorkingMinutes = getTotalWorkingTimeInMinutes(isDayAvailable) * numberOfTables;
        }
        if(totalWorkingMinutes <= 0){
            continue;
        }
        for(Order order : orderList){
            if(order.getStartTime().getDayOfYear() == isDayAvailable.getDayOfYear()){
                long minutesToSubtract = Duration.between(order.getStartTime(), order.getEndTime()).toMinutes();
                totalWorkingMinutes -= (int) minutesToSubtract;
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

    private int getTotalWorkingTimeInMinutesForCurrentDay(LocalDateTime checkDay,int currentTimeInMinutes,int durationOfReservaton){
        int workingMinutes = 0;
        switch(checkDay.getDayOfWeek()) {
            case SATURDAY:
                if(currentTimeInMinutes + durationOfReservaton > convertToMinutes(workSaturdayEnd)){
                    workingMinutes = 0;
                }
                else if(currentTimeInMinutes > convertToMinutes(workSaturdayStart)){
                    workingMinutes = convertToMinutes(workSaturdayEnd) - currentTimeInMinutes;
                }
                else{
                    workingMinutes = convertToMinutes(workSaturdayEnd) - convertToMinutes(workSaturdayStart);
                }
                break;
            case SUNDAY:
                if(currentTimeInMinutes + durationOfReservaton > convertToMinutes(workSundayEnd)){
                    workingMinutes = 0;
                }
                else if(currentTimeInMinutes > convertToMinutes(workSundayStart)){
                    workingMinutes = convertToMinutes(workSundayEnd) - currentTimeInMinutes;
                }
                else{
                    workingMinutes = convertToMinutes(workSundayEnd) - convertToMinutes(workSundayStart);
                }
                break;
            default:
                if(currentTimeInMinutes + durationOfReservaton > convertToMinutes(workWeekEnd)){
                    workingMinutes = 0;
                }
                else if(currentTimeInMinutes > convertToMinutes(workWeekStart)){
                    workingMinutes = convertToMinutes(workWeekEnd) - currentTimeInMinutes;
                }
                else{
                    workingMinutes = convertToMinutes(workWeekEnd) - convertToMinutes(workWeekStart);
                }
                break;
        }
        return workingMinutes;


    }
    private static boolean isSlotAvailable(LocalDateTime startTime, LocalDateTime endTime, int table, ArrayList<SuggestedReservationSlot> reservedSlots) {
        for (SuggestedReservationSlot reservedSlot : reservedSlots) {
            if (reservedSlot.getTable().equals(table)
                    && reservedSlot.getStartDate().isBefore(endTime)
                    && reservedSlot.getEndDate().isAfter(startTime)) {
                return false;
            }
        }
        return true;
    }
    public static int convertToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }
}
