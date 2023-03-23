package com.payten.FoodRest.controller;

import com.payten.FoodRest.model.Customers;
import com.payten.FoodRest.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CustomersController {

    @Autowired
    private CustomersRepository customersRepository;

    @PutMapping("/addListItem")
    public ResponseEntity<Customers> addItem(@RequestParam String viberId, @RequestParam String newItem, @RequestParam Double price, @RequestParam Double duration) {
        Optional<Customers> doc = customersRepository.findById(viberId);
        if (doc.isEmpty()) {
            doc = Optional.of(new Customers(viberId, new ArrayList<>(), new ArrayList<>(), 0.0, null, 0.0,0.0,false));
        }
        doc.get().setCurrentPrice(doc.get().getCurrentPrice() + price);
        doc.get().setDurationMin(doc.get().getDurationMin() + duration);
        doc.get().getCurrentOrder().add(newItem);
        return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
    }

    @DeleteMapping("/removeListItem")
    public ResponseEntity<Customers> removeListItem(@RequestParam String viberId, @RequestParam String newItem, @RequestParam Double price, @RequestParam Double duration) {
        Optional<Customers> doc = customersRepository.findById(viberId);
        doc.get().setCurrentPrice(doc.get().getCurrentPrice() - price);
        doc.get().setDurationMin(doc.get().getDurationMin() - duration);
        doc.get().getCurrentOrder().remove(newItem);
        return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
    }
    @PutMapping("/changePayingStatus")
    public ResponseEntity<Customers> changePayingStatus(@RequestParam String viberId, @RequestParam Boolean payingStatus){
        Optional<Customers> doc = customersRepository.findById(viberId);
        doc.get().setIsPaying(payingStatus);
        return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
    }
    @GetMapping("/getIsPayingStatus/{viberId}")
    public ResponseEntity<Boolean> getIsPayingStatus(@PathVariable(value = "viberId") String viberId) {
        if (customersRepository.existsById(viberId) && customersRepository.findById(viberId).get().getIsPaying().equals(true)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }
    @GetMapping("/getTotalTime/{viberId}")
    public ResponseEntity<Double> getTotalTime(@PathVariable(value = "viberId") String viberId) {
            return new ResponseEntity<>(customersRepository.findById(viberId).get().getDurationMin(), HttpStatus.OK);
    }
    @GetMapping("/getListByViberId")
    public ResponseEntity<List<String>> findByViber(@RequestParam String viberId) {
        Optional<Customers> customers = customersRepository.findById(viberId);
        ArrayList<String> response = new ArrayList<>();
        response = customers.get().getCurrentOrder();
        response.add("Ukupno za uplatu: "+ customers.get().getCurrentPrice());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //Used for checking if user added some of services to cart
    @GetMapping("/getActiveOrders")
    public ResponseEntity<Boolean> checkCurrentOrder(@RequestParam String viberId) {
        if(customersRepository.existsById(viberId) && customersRepository.findById(viberId).get().getCurrentOrder().size() > 0){
            return new ResponseEntity<>(true,HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }

}
