package com.payten.FoodRest.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.payten.FoodRest.model.Order;
import com.payten.FoodRest.model.OrderState;
import com.payten.FoodRest.repository.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/v1")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/getOrders")
    public ResponseEntity<List<Order>> findAll(){
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PostMapping("/addOrder")
    public ResponseEntity<Order> save(@RequestBody Order order){
        order.setCreationTime(LocalDateTime.now());
        return ResponseEntity.ok(orderRepository.save(order));
    }

    @PutMapping("/acceptOrder/{id}")
    public ResponseEntity<Order> acceptOrder(@PathVariable("id") String id){
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (!existingOrder.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Order updatedOrder = existingOrder.get();
        updatedOrder.setState(OrderState.IN_PROGRESS);

        orderRepository.save(updatedOrder);

        return new ResponseEntity<>(orderRepository.save(updatedOrder), HttpStatus.OK);
    }

    @PutMapping("/declineOrder/{id}")
    public ResponseEntity<Order> declineOrder(@PathVariable("id") String id){
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (!existingOrder.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Order updatedOrder = existingOrder.get();
        updatedOrder.setState(OrderState.DECLINED);

        orderRepository.save(updatedOrder);

        return new ResponseEntity<>(orderRepository.save(updatedOrder), HttpStatus.OK);
    }
}
