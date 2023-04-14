package com.payten.FoodRest.controller;

import com.payten.FoodRest.model.Customers;
import com.payten.FoodRest.model.Menu;
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

    //Adding item to cart, if cart is empty we are going to create Customer entry first
    @PutMapping("/addItemToCart")
    public ResponseEntity<Customers> addItem(@RequestParam String viberId, @RequestBody Menu newItem) {
        Optional<Customers> doc = customersRepository.findById(viberId);
        if (doc.isEmpty()) {
            doc = Optional.of(new Customers(viberId, new ArrayList<>(),new ArrayList<>(), null,false));
        }
        doc.get().getCurrentOrder().add(newItem);
        return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
    }

    @DeleteMapping("/removeItemFromCart")
    public ResponseEntity<Customers> removeListItem(@RequestParam String viberId, @RequestBody Menu removeItem) {
        Optional<Customers> doc = customersRepository.findById(viberId);
        if(doc.get().getCurrentOrder().contains(removeItem)){
            doc.get().getCurrentOrder().remove(removeItem);
            return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
    }
    @PutMapping("/changePayingStatus")
    public ResponseEntity<Customers> changePayingStatus(@RequestParam String viberId, @RequestParam Boolean payingStatus){
        Optional<Customers> doc = customersRepository.findById(viberId);
        doc.get().setIsPaying(payingStatus);
        return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
    }
    @PutMapping("/clearCart")
    public ResponseEntity<Customers> completeOrder(@RequestParam String viberId){
        Optional<Customers> doc = customersRepository.findById(viberId);
        doc.get().getArchievedOrder().addAll(doc.get().getCurrentOrder());
        doc.get().setCurrentOrder(new ArrayList<>());
        return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
    }
    @GetMapping("/checkPayingStatus")
    public ResponseEntity<Boolean> getIsPayingStatus(@RequestParam String viberId) {
        if (customersRepository.existsById(viberId) && customersRepository.findById(viberId).get().getIsPaying().equals(true)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }
    @GetMapping("/getTotalTime")
    public ResponseEntity<Integer> getTotalTime(@RequestParam String viberId) {
            Integer totalTime = 0;
            for (Menu menu : customersRepository.findById(viberId).get().getCurrentOrder()){
                totalTime += menu.getTime();
            }
            return new ResponseEntity<>(totalTime, HttpStatus.OK);
    }
    @GetMapping("/getTotalPrice")
    public ResponseEntity<Double> getTotalPrice(@RequestParam String viberId) {
        Double totalPrice = 0.0;
        for (Menu menu : customersRepository.findById(viberId).get().getCurrentOrder()){
            totalPrice += menu.getPrice();
        }
        return new ResponseEntity<>(totalPrice, HttpStatus.OK);
    }
    //Using this endpoint for creating Orders for Android POS
    @GetMapping("/convertToOrderModel")
    public ResponseEntity<List<String>> getListForOrderByViberId(@RequestParam String viberId) {
        Optional<Customers> customers = customersRepository.findById(viberId);
        ArrayList<String> response = new ArrayList<>();
        for (Menu menu : customersRepository.findById(viberId).get().getCurrentOrder()){
            response.add(menu.getName());
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //Using this endpoint for creating Asseco payment.
    @GetMapping("/assecoOrderConverter")
    public ResponseEntity<List<Menu>> getCustomerCartByViberId(@RequestParam String viberId) {
        Optional<Customers> customers = customersRepository.findById(viberId);
        return new ResponseEntity<>(customers.get().getCurrentOrder(), HttpStatus.OK);
    }
    //Using this endpoint for generating current cart list for Viber Bot
    @GetMapping("/getCart")
    public ResponseEntity<List<String>> findByViber(@RequestParam String viberId) {
        Optional<Customers> customers = customersRepository.findById(viberId);
        ArrayList<String> response = new ArrayList<>();
        Double totalPrice = 0.0;
        for (Menu menu : customersRepository.findById(viberId).get().getCurrentOrder()){
            response.add(menu.getName()+"\n"+"CENA: "+ menu.getPrice()+"RSD");
            totalPrice += menu.getPrice();
        }
        response.add("Ukupno za uplatu: "+ totalPrice);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //Used for checking if user added some of services to cart
    @GetMapping("/checkIfCartIsEmpty")
    public ResponseEntity<Boolean> checkCurrentOrder(@RequestParam String viberId) {
        if(customersRepository.existsById(viberId) && customersRepository.findById(viberId).get().getCurrentOrder().size() > 0){
            return new ResponseEntity<>(true,HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.OK);
    }

}
