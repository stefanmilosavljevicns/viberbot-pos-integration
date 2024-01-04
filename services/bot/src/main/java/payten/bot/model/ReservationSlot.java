package payten.bot.model;

import java.time.LocalDateTime;

public class ReservationSlot {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer table;

    public ReservationSlot(LocalDateTime startDate, LocalDateTime endDate, Integer table) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.table = table;
    }
    public ReservationSlot(){
        
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getTable() {
        return table;
    }

    public void setTable(Integer table) {
        this.table = table;
    }
}
