package com.payten.FoodRest.controller;

import com.payten.FoodRest.model.Order;
import com.payten.FoodRest.model.OrderState;
import com.payten.FoodRest.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/getOrders")
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PostMapping("/addOrder")
    public ResponseEntity<Order> save(@RequestBody Order order) {
        return ResponseEntity.ok(orderRepository.save(order));
    }

    @GetMapping("/checkAvailability")
    public ResponseEntity<String> checkAvailability(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                    @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Order> conflictingReservations = orderRepository.findByStartTimeLessThanAndEndTimeGreaterThan(end, start);

        if (conflictingReservations.isEmpty()) {
            return ResponseEntity.ok("Time slot is available.");
        } else {
            // Find the next available time slot greater than 'end' or less than 'start'
            LocalDateTime nextAvailableStart = null;
            LocalDateTime nextAvailableEnd = null;

            for (Order reservation : conflictingReservations) {
                if (reservation.getStartTime().isBefore(start)) {
                    nextAvailableEnd = reservation.getStartTime();
                } else {
                    nextAvailableStart = reservation.getEndTime();
                    break;
                }
            }
            if (nextAvailableStart != null && nextAvailableEnd != null) {
                return ResponseEntity.ok("Time slot is not available. Next available time slot is from "
                        + nextAvailableStart.toString() + " to " + nextAvailableEnd.toString());
            } else if (nextAvailableStart != null) {
                return ResponseEntity.ok("Time slot is not available. Next available time slot is after "
                        + nextAvailableStart.toString());
            } else if (nextAvailableEnd != null) {
                return ResponseEntity.ok("Time slot is not available. Next available time slot is before "
                        + nextAvailableEnd.toString());
            } else {
                return ResponseEntity.ok("Time slot is not available. There are no available time slots.");
            }
        }
    }

    @PutMapping("/acceptOrder/{id}")
    public ResponseEntity<Order> acceptOrder(@PathVariable("id") String id) {
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
    public ResponseEntity<Order> declineOrder(@PathVariable("id") String id) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (!existingOrder.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Order updatedOrder = existingOrder.get();
        updatedOrder.setState(OrderState.DECLINED);

        orderRepository.save(updatedOrder);

        return new ResponseEntity<>(orderRepository.save(updatedOrder), HttpStatus.OK);
    }

    @PutMapping("/completeOrder/{id}")
    public ResponseEntity<Order> completeOrder(@PathVariable("id") String id) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (!existingOrder.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Order updatedOrder = existingOrder.get();
        updatedOrder.setState(OrderState.COMPLETED);

        orderRepository.save(updatedOrder);

        return new ResponseEntity<>(orderRepository.save(updatedOrder), HttpStatus.OK);
    }


}
