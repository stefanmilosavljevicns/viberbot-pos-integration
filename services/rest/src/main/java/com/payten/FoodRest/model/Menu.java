package com.payten.FoodRest.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Menu")
public class Menu {
    private String name;
    private Double price;
    private String description;
    private Integer time;
    private String category;
}
