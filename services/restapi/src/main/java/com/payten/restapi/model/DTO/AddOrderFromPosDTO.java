package com.payten.restapi.model.DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AddOrderFromPosDTO {
    private ArrayList<String> description;
    private LocalDateTime startTime;
    private String customerName;

    public AddOrderFromPosDTO() {
    }

    public AddOrderFromPosDTO(ArrayList<String> description, LocalDateTime startTime, String customerName) {
        this.description = description;
        this.startTime = startTime;
        this.customerName = customerName;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
