package com.payten.restapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Order")
public class Order {
    @Id
    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private OrderState state;
    private String customerName;
    private String viberID;

    public Order(LocalDateTime startTime, LocalDateTime endTime, OrderState state, String customerName, String viberID) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
        this.customerName = customerName;
        this.viberID = viberID;
    }
}
