package com.payten.FoodRest.controller;

import com.payten.FoodRest.model.Menu;
import com.payten.FoodRest.model.Order;
import com.payten.FoodRest.model.OrderState;
import com.payten.FoodRest.repository.MenuRepository;
import com.payten.FoodRest.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("${rest.path}")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MenuRepository menuRepository;
    @GetMapping("/getOrders")
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderRepository.findAll());
    }


    @PostMapping("/addOrder")
    public ResponseEntity<Order> save(@RequestBody Order order) {
        return ResponseEntity.ok(orderRepository.save(order));
    }
    @PostMapping("/POSReservation")
    public ResponseEntity<Order> posReservation(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, @RequestBody ArrayList<String> service) {
        Order order = new Order();
        ArrayList<String> listOfServices = new ArrayList<>();
        order.setStartTime(start);
        order.setViberID(null);
        order.setPrice(0.0);
        order.setState(OrderState.IN_PROGRESS);
        for(String menuItem : service){
            //Proveravam sa Pex-om jel moze da prihvati vise usluga sa jednim zakazivanjem, ako ne moze castovacu ovo u String
            Menu menu = menuRepository.findByName(menuItem);
            if(menu.getName() != null){
                listOfServices.add(menu.getName());
                order.setPrice(order.getPrice()+menu.getPrice());
                order.setEndTime(start.plusMinutes(menu.getTime()));
            }
            else{
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        order.setDescription(listOfServices);
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
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/checkFreeTimeSlots")
    public List<LocalDateTime> getDateTimes(
            @RequestParam("localDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate,
            @RequestParam("totalMinutes") int totalMinutes
    ) {
        LocalTime startTime = LocalTime.of(7, 0);  // 7am
        LocalTime endTime = LocalTime.of(17, 0);   // 5pm

        LocalDateTime dateTime = localDate.atTime(startTime);
        List<LocalDateTime> result = new ArrayList<>();

        while (dateTime.isBefore(localDate.atTime(endTime))) {
            result.add(dateTime);
            dateTime = dateTime.plusMinutes(totalMinutes);
        }

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
    @GetMapping("/checkTimeSlotAvailability")
    public ResponseEntity<String> checkAvailability(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                    @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Order> conflictingReservations = orderRepository.findByStartTimeLessThanAndEndTimeGreaterThan(end, start);

        if (conflictingReservations.isEmpty()) {
            return ResponseEntity.ok("Time slot is available.");
        } else {
            return ResponseEntity.ok("Time slot is not available.");
        }
    }
    @PutMapping("/clearCart/{id}")
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

    //Update-ujemo termin tako sto unesemo viberId od korisnikovog termina koji zelimo da promenimo. Na osnovu viberId-a dobijamo instancu Order objekta. U njemu prvo uzimamo ukupnu duzinu termina (od startTime-a do endTime-a) zatim prosledjujemo
    // iz query parametra novi startTime a endTime dobijamo uvecavanjem novog starTtime-a za ukupnu duzinu termina koju smo prethodno izracunali (promenljiva reservationDuration)
    @PutMapping("/updateStartTime")
    public ResponseEntity<Order> updateStartTime(@RequestBody String start,
                                                 @RequestParam("orderID") String viberId) throws URISyntaxException {
        Optional<Order> existingOrder = Optional.ofNullable(orderRepository.findByIdAndCheckTime(viberId));
        if (existingOrder.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        Duration reservationDuration = Duration.between(existingOrder.get().getStartTime(), existingOrder.get().getEndTime());
        Order updatedOrder = existingOrder.get();
        updatedOrder.setStartTime(startDate);
        updatedOrder.setEndTime(startDate.plusMinutes(reservationDuration.toMinutes()));
        if(updatedOrder.getViberID() != null){
            logger.info("Inside loop");
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            URI uri = new URI("http://bot:9943/dentalcare-bot?viberId=" + URLEncoder.encode(updatedOrder.getViberID(), StandardCharsets.UTF_8) +"&startDate="+startDate);
            HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
            ResponseEntity<String> responseEntityPut = restTemplate.exchange(uri, HttpMethod.PUT, requestEntity, String.class);
            logger.info(responseEntityPut.getStatusCode().toString() + responseEntityPut.getBody());
        }
        orderRepository.save(updatedOrder);
        return new ResponseEntity<>(orderRepository.save(updatedOrder), HttpStatus.OK);
    }


}
