package com.payten.FoodRest.model;

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
    private String description;
    private Double price;
    private LocalDateTime pickupTime; 
    private LocalDateTime creationTime; 
    private OrderState state;
    private String viberID;
}
