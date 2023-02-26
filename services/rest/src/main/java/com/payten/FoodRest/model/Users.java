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
@Document(collection = "Users")
public class Users {
    @Id
    private String id;
    private Integer pin;
    private String name;
    private String lastName;
    private String email;
    private LocalDateTime creationDate;
    private String nickname;
    private String location;
    private String role;
}
