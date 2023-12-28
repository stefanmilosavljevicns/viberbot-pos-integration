package com.payten.restapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "CustomerPreferences")
public class CustomerPreferences {
    @Id
    private String id;
    private CustomerLocale customerLocale;
}
