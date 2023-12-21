package com.payten.restapi.controller;

import com.payten.restapi.model.Customers;
import com.payten.restapi.model.Menu;
import com.payten.restapi.repository.CustomersRepository;
import com.payten.restapi.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.payten.restapi.util.Constants.controllerLogFormat;

@RestController
@RequestMapping("/api/v1")
public class CustomersController {

    private static final Logger logger = LoggerFactory.getLogger(CustomersController.class);
    @Value("${customers.max.capacity}")
    private int maxCartCapacity;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private OrderRepository orderRepository;
    @PutMapping("/addItemToCart")
    public ResponseEntity<Customers> addItemToCart(@RequestParam String viberId, @RequestBody Menu newItem) {
        Optional<Customers> doc = customersRepository.findById(viberId);
        if (doc.isEmpty()) {
            doc = Optional.of(new Customers(viberId, new ArrayList<>(),new ArrayList<>(), null,false));
        }
        if(doc.get().getCurrentOrder().size() < maxCartCapacity){
            doc.get().getCurrentOrder().add(newItem);
            logger.info(String.format(controllerLogFormat,"addItemTocart", HttpStatus.OK));
            return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
        }
        else{
            logger.info(String.format(controllerLogFormat, "addItemTocart", HttpStatus.FORBIDDEN));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


    }

    @DeleteMapping("/removeItemFromCart")
    public ResponseEntity<Customers> removeItemFromCart(@RequestParam String viberId, @RequestBody Menu removeItem) {
        Optional<Customers> doc = customersRepository.findById(viberId);
        if(doc.get().getCurrentOrder().contains(removeItem)){
            doc.get().getCurrentOrder().remove(removeItem);
            logger.info(String.format(controllerLogFormat,"/removeItemFromCart", HttpStatus.OK));
            return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
        }
        logger.info(String.format(controllerLogFormat,"/removeItemFromCart", HttpStatus.OK));
        return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
    }
    @PutMapping("/clearCart")
    public ResponseEntity<Customers> clearCart(@RequestParam String viberId){
        Optional<Customers> doc = customersRepository.findById(viberId);
        doc.get().getArchievedOrder().addAll(doc.get().getCurrentOrder());
        doc.get().setCurrentOrder(new ArrayList<>());
        logger.info(String.format(controllerLogFormat,"/clearCart", HttpStatus.OK));
        return new ResponseEntity<>(customersRepository.save(doc.get()), HttpStatus.OK);
    }
    @GetMapping("/getTotalTime")
    public ResponseEntity<Integer> getTotalTime(@RequestParam String viberId) {
            Integer totalTime = 0;
            for (Menu menu : customersRepository.findById(viberId).get().getCurrentOrder()){
                totalTime += menu.getTime();
            }
        logger.info(String.format(controllerLogFormat,"/getTotalTime", HttpStatus.OK));
        return new ResponseEntity<>(totalTime, HttpStatus.OK);
    }
    @GetMapping("/getTotalPrice")
    public ResponseEntity<Double> getTotalPrice(@RequestParam String viberId) {
        Double totalPrice = 0.0;
        for (Menu menu : customersRepository.findById(viberId).get().getCurrentOrder()){
            totalPrice += menu.getPrice();
        }
        logger.info(String.format(controllerLogFormat,"/getTotalPrice", HttpStatus.OK));
        return new ResponseEntity<>(totalPrice, HttpStatus.OK);
    }
    @GetMapping("/convertToOrderModel")
    public ResponseEntity<List<String>> convertToOrderModel(@RequestParam String viberId) {
        Optional<Customers> customers = customersRepository.findById(viberId);
        ArrayList<String> response = new ArrayList<>();
        for (Menu menu : customersRepository.findById(viberId).get().getCurrentOrder()){
            response.add(menu.getName());
        }
        logger.info(String.format(controllerLogFormat,"/convertToOrderModel", HttpStatus.OK));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //Using this endpoint for generating current cart list for Viber Bot
    @GetMapping("/getCart")
    public ResponseEntity<List<String>> getCart(@RequestParam String viberId) {
        Optional<Customers> customers = customersRepository.findById(viberId);
        ArrayList<String> response = new ArrayList<>();
        Double totalPrice = 0.0;
        for (Menu menu : customersRepository.findById(viberId).get().getCurrentOrder()){
            response.add(menu.getName()+"\n"+"CENA: "+ menu.getPrice()+"RSD");
            totalPrice += menu.getPrice();
        }
        response.add("UKUPNO: "+ totalPrice);
        logger.info(String.format(controllerLogFormat,"/getCart",response, HttpStatus.OK));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //Used for checking if user added some of services to cart
    @GetMapping("/checkIfUserCanOrder")
    public ResponseEntity<Boolean> checkIfUserCanOrder(@RequestParam String viberId) {
        if(orderRepository.findByViberId(viberId).equals(null)){
            logger.info(String.format(controllerLogFormat,"/checkIfUserCanOrder",true, HttpStatus.OK));
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        else if(customersRepository.existsById(viberId) && customersRepository.findById(viberId).get().getCurrentOrder().size() > 0){
            logger.info(String.format(controllerLogFormat,"/checkIfUserCanOrder",true, HttpStatus.OK));
            return new ResponseEntity<>(true,HttpStatus.OK);
        }
        logger.info(String.format(controllerLogFormat,"/checkIfUserCanOrder",false, HttpStatus.OK));
        return new ResponseEntity<>(false, HttpStatus.OK);
    }

}
