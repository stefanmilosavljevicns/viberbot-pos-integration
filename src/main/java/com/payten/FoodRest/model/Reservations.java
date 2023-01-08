package com.payten.FoodRest.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Reservations")
public class Reservations {
    @Id
    private String id;
    private String name;
    private Boolean paid;
    private String reservation;
    private String worker;
    private Boolean bot;
}