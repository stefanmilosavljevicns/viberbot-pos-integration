package com.payten.FoodRest.model;

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
    private ArrayList<String> description;
    private Double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private OrderState state;
    private String viberID;
}
