package com.payten.FoodRest.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Customers")
public class Customers {
    @Id
    private String id;
    private ArrayList<String> currentOrder;
    private ArrayList<String> archievedOrder;
    private Double totalSpent;
    private String token;
    private Double currentPrice;
    private Double durationMin;
    private Boolean isPaying;
}
