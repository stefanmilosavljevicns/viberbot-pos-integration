package com.payten.restapi.repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.payten.restapi.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

    public interface OrderRepository extends MongoRepository<Order, String> {
        @Query("{ 'startTime': { $gt: ?0, $lt: ?1 } }")
        List<Order> findUsersToRemindForReservation(LocalDateTime start, LocalDateTime end);
        @Query(value = "{'viberID' : ?0, 'state' : 'PENDING' }")
        List<Order> checkIfUserCanOrder(String name);
        @Query(value = "{'viberID' : ?0, 'state' : { $in: [ 'COMPLETED', 'DECLINED' ] } }")
        List<Order> findByViberId(String name);
        @Query(value = "{'state' : { $in: [ 'PENDING', 'IN_PROGRESS' ] } }")
        ArrayList<Order> findActiveReservations();
        @Query("{ 'startTime': { $gte: ?0, $lt: ?1 }, 'state' : { $in: [ 'PENDING', 'IN_PROGRESS' ] } }")
        ArrayList<Order> findPossibleTimeSlots(LocalDateTime startOfDay, LocalDateTime endOfDay);

        default ArrayList<Order> findOrdersByDate(LocalDate date) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);
            return findPossibleTimeSlots(startOfDay, endOfDay);
        }
    }