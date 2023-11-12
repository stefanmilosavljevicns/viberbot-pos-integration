package payten.bot.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrderPOS {
    private ArrayList<String> description;
    private Double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String state;
    private String viberID;

    public OrderPOS(ArrayList<String> description, Double price, LocalDateTime startTime, LocalDateTime endTime, String state, String viberID) {
        this.description = description;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
        this.viberID = viberID;
    }

    public OrderPOS() {
        // Default constructor for JSON deserilization
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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
