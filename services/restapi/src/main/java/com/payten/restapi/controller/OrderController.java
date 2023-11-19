package com.payten.restapi.controller;

import com.payten.restapi.model.Order;
import com.payten.restapi.model.OrderState;
import com.payten.restapi.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.payten.restapi.util.Constants.controllerLogFormat;


@RestController
@RequestMapping("${rest.path}")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/getOrders")
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderRepository.findAll());
    }


    @PostMapping("/addOrder")
    public ResponseEntity<Order> save(@RequestBody Order order) {
        logger.info(String.format(controllerLogFormat, "addOrder", order, HttpStatus.OK));
        return ResponseEntity.ok(orderRepository.save(order));
    }
    @GetMapping("/getAllActiveDates")
    public ResponseEntity<List<Order>> getOrdersWithin24Hours() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime endTime = startTime.plusDays(1);
        List<Order> orders = orderRepository.findUsersToRemindForReservation(startTime, endTime);
        logger.info(String.format(controllerLogFormat, "getAllActiveDates", orders, HttpStatus.OK));
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/checkFreeTimeSlots")
    public List<LocalDateTime> getDateTimes(
            @RequestParam("localDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate,
            @RequestParam("totalMinutes") int totalMinutes
    ) {
        //todo ovo nek vuce iz app properties-a kada setuje radno vreme
        LocalTime startTime = LocalTime.of(7, 0);  // 7am
        LocalTime endTime = LocalTime.of(17, 0);   // 5pm

        LocalDateTime dateTime = localDate.atTime(startTime);
        List<LocalDateTime> result = new ArrayList<>();

        while (dateTime.isBefore(localDate.atTime(endTime))) {
            result.add(dateTime);
            dateTime = dateTime.plusMinutes(totalMinutes);
        }
        logger.info(String.format(controllerLogFormat, "checkFreeTimeSlots", result, HttpStatus.OK));
        return result;
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
    @GetMapping("/checkTimeSlotAvailability")
    public ResponseEntity<String> checkAvailability(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                    @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Order> conflictingReservations = orderRepository.findByStartTimeLessThanAndEndTimeGreaterThan(end, start);

        if (conflictingReservations.isEmpty()) {
            logger.info(String.format(controllerLogFormat, "checkTimeSlotAvailability", "Time slot is available.", HttpStatus.OK));
            return ResponseEntity.ok("Time slot is available.");
        } else {
            logger.info(String.format(controllerLogFormat, "checkTimeSlotAvailability", "Time slot is not available.", HttpStatus.OK));
            return ResponseEntity.ok("Time slot is not available.");
        }
    }
    @PutMapping("/clearCart/{id}")
    public ResponseEntity<Order> completeOrder(@PathVariable("id") String id) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (!existingOrder.isPresent()) {
            logger.info(String.format(controllerLogFormat, "clearCart", "", HttpStatus.NOT_FOUND));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Order updatedOrder = existingOrder.get();
        updatedOrder.setState(OrderState.COMPLETED);

        orderRepository.save(updatedOrder);
        logger.info(String.format(controllerLogFormat, "clearCart", "", HttpStatus.OK));
        return new ResponseEntity<>(orderRepository.save(updatedOrder), HttpStatus.OK);
    }

    //Update-ujemo termin tako sto unesemo viberId od korisnikovog termina koji zelimo da promenimo. Na osnovu viberId-a dobijamo instancu Order objekta. U njemu prvo uzimamo ukupnu duzinu termina (od startTime-a do endTime-a) zatim prosledjujemo
    // iz query parametra novi startTime a endTime dobijamo uvecavanjem novog starTtime-a za ukupnu duzinu termina koju smo prethodno izracunali (promenljiva reservationDuration)
    @PutMapping("/updateStartTime")
    public ResponseEntity<Order> updateStartTime(@RequestParam("startDate") String start,
                                                 @RequestParam("viberId") String viberId) {
        Optional<Order> existingOrder = Optional.ofNullable(orderRepository.findByViberId(viberId));
        if (existingOrder.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        Duration reservationDuration = Duration.between(existingOrder.get().getStartTime(), existingOrder.get().getEndTime());
        Order updatedOrder = existingOrder.get();
        updatedOrder.setStartTime(startDate);
        updatedOrder.setEndTime(startDate.plusMinutes(reservationDuration.toMinutes()));
        orderRepository.save(updatedOrder);
        logger.info(String.format(controllerLogFormat, "updateStartTime", "updatedOrder", HttpStatus.OK));
        return new ResponseEntity<>(orderRepository.save(updatedOrder), HttpStatus.OK);
    }


}
