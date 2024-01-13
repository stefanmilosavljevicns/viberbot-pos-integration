package com.payten.restapi.model;

import com.payten.restapi.model.enums.OrderState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

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
    private Integer tableNumber;
    private String viberID;

    public Order(LocalDateTime startTime, LocalDateTime endTime, OrderState state, String customerName, String viberID, Integer tableNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
        this.customerName = customerName;
        this.viberID = viberID;
        this.tableNumber = tableNumber;
    }
}
