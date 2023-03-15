package com.payten.FoodRest.controller;

import com.payten.FoodRest.model.Customers;
import com.payten.FoodRest.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1")
public class CustomersController {

    @Autowired
    private CustomersRepository customersRepository;

    @PostMapping("/myendpoint")
    public Customers addItem(@RequestParam String itemId, @RequestParam String newItem) {
            Customers doc = customersRepository.findById(itemId,Customers.class);
        if (doc == null) {
            doc = new Customers(new ArrayList<>());
        }
        doc.getCurrentOrder().add(newItem);
        customersRepository.save(doc);
        return doc;
    }
}
