package payten.bot.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrderPOS {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String state;
    private String viberID;
    private String customerName;
    private Integer tableNumber;


    public OrderPOS(LocalDateTime startTime, LocalDateTime endTime, String state,String customerName, Integer tableNumber,String viberID) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
        this.customerName = customerName;
        this.tableNumber = tableNumber;
        this.viberID = viberID;
    }


    public int getTable() {
        return tableNumber;
    }

    public void setTable(Integer table) {
        this.tableNumber = table;
    }

    public OrderPOS() {
        // Default constructor for JSON deserilization
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getViberID() {
        return viberID;
    }

    public void setViberID(String viberID) {
        this.viberID = viberID;
    }
}
