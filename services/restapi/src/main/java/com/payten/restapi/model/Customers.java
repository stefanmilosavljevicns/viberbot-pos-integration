package com.payten.restapi.model;
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
    private ArrayList<Menu> currentOrder;
    private ArrayList<Menu> archievedOrder;
    private String token;
    private Boolean isPaying;
}
