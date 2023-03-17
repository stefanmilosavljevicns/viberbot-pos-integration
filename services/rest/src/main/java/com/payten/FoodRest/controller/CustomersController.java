package com.payten.FoodRest.controller;

import com.payten.FoodRest.model.Customers;
import com.payten.FoodRest.model.Users;
import com.payten.FoodRest.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CustomersController {

    @Autowired
    private CustomersRepository customersRepository;

    @PutMapping("/addListItem")
    public ResponseEntity<Customers> addItem(@RequestParam String viberId, @RequestParam String newItem, @RequestParam Double price) {
            Optional<Customers> doc = customersRepository.findById(viberId);
        if (doc.isEmpty()) {
            doc = Optional.of(new Customers(viberId,new ArrayList<>(),new ArrayList<>(),0.0,null,0.0));
        }
        doc.get().setCurrentPrice(doc.get().getCurrentPrice() + price);
        doc.get().getCurrentOrder().add(newItem);
        return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
    }
    @PatchMapping("/removeListItem")
    public ResponseEntity<Customers> removeListItem(@RequestParam String viberId, @RequestParam String newItem, @RequestParam Double price) {
        Optional<Customers> doc = customersRepository.findById(viberId);
        doc.get().setCurrentPrice(doc.get().getCurrentPrice() - price);
        doc.get().getCurrentOrder().remove(newItem);
        return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
    }


}
