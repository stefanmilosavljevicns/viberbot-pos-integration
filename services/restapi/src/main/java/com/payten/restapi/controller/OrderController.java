package com.payten.restapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payten.restapi.model.Order;
import com.payten.restapi.model.OrderState;
import com.payten.restapi.repository.OrderRepository;
import com.payten.restapi.util.ReservationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.payten.restapi.util.Constants.controllerLogFormat;


@RestController
@RequestMapping("${rest.path}")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SimpMessagingTemplate msgTemplate;

    @Autowired
    private ReservationUtil reservationUtil;

    @GetMapping("/getOrders")
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderRepository.findAll());
    }



    @SendTo("/topic/order")
    @PostMapping("/addOrder")
    public ResponseEntity<Order> save(@RequestBody Order order) throws JsonProcessingException {
        logger.info(String.format(controllerLogFormat, "addOrder", order, HttpStatus.OK));
        // Creating a Map of key-value pairs
        serializeOrderForSending(order.getId());
        return ResponseEntity.ok(orderRepository.save(order));
    }
    @GetMapping("/historyOfReservation")
    public ResponseEntity<List<Order>> historyOfReservation(@RequestParam("viberId") String viberId) {
        List<Order> orders = orderRepository.findByViberId(viberId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/findAvailableDays")
    public ResponseEntity<List<LocalDate>> findAvailableDays(@RequestParam("durationMinutes") Integer durationMinutes) {
        ArrayList<Order> orders = orderRepository.findActiveReservations();
        ArrayList<LocalDate> availableDays = reservationUtil.getAvailableDaysForReservation(orders,durationMinutes);
        return new ResponseEntity<>(availableDays, HttpStatus.OK);
    }

    @GetMapping("/findAvailableTimeSlotsForReservation")
    public ResponseEntity<List<LocalDate>> findAvailableTimeSlotsForReservation(@RequestParam("targetDate") String targetDate) {
        String[] deserializeParameter = targetDate.split(":");
        ArrayList<Order> orders = orderRepository.findOrdersByDate(LocalDate.parse(deserializeParameter[0]));

        return new ResponseEntity<>(availableDays, HttpStatus.OK);
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
        logger.info(String.format(controllerLogFormat, "acceptOrder", updatedOrder, HttpStatus.OK));
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
        logger.info(String.format(controllerLogFormat, "declineOrder", updatedOrder, HttpStatus.OK));
        return new ResponseEntity<>(orderRepository.save(updatedOrder), HttpStatus.OK);
    }



    //Update-ujemo termin tako sto unesemo viberId od korisnikovog termina koji zelimo da promenimo. Na osnovu viberId-a dobijamo instancu Order objekta. U njemu prvo uzimamo ukupnu duzinu termina (od startTime-a do endTime-a) zatim prosledjujemo
    // iz query parametra novi startTime a endTime dobijamo uvecavanjem novog starTtime-a za ukupnu duzinu termina koju smo prethodno izracunali (promenljiva reservationDuration)
    @PutMapping("/updateStartTime")
    public ResponseEntity<Order> updateStartTime(@RequestParam("startDate") String start,
                                                 @RequestParam("orderId") String orderId) {
        Optional<Order> existingOrder = orderRepository.findById(orderId);
        if (existingOrder.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        Duration reservationDuration = Duration.between(existingOrder.get().getStartTime(), existingOrder.get().getEndTime());
        Order updatedOrder = existingOrder.get();
        updatedOrder.setStartTime(startDate);
        updatedOrder.setEndTime(startDate.plusMinutes(reservationDuration.toMinutes()));
        logger.info(String.format(controllerLogFormat, "updateStartTime", "updatedOrder", HttpStatus.OK));
        return new ResponseEntity<>(orderRepository.save(updatedOrder), HttpStatus.OK);
    }

    private void serializeOrderForSending(String orderId) throws JsonProcessingException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("orderId", orderId);

        // Using Jackson ObjectMapper to serialize Map to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(jsonMap);

        msgTemplate.convertAndSend("/topic/order", jsonString);
    }
}
