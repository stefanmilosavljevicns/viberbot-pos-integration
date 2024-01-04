package com.payten.restapi.repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.payten.restapi.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

    public interface OrderRepository extends MongoRepository<Order, String> {
        @Query("{ 'startTime': { $gt: ?0, $lt: ?1 } }")
        List<Order> findUsersToRemindForReservation(LocalDateTime start, LocalDateTime end);          
        List<Order> findByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime end, LocalDateTime start);
        List<Order> findByStartTimeGreaterThan(LocalDateTime twentyFourHoursAgo);
        @Query(value = "{ 'viberID' : ?0, 'state' : { $in: [ 'COMPLETED', 'DECLINED' ] } }")
        List<Order> findByViberId(String name);
        @Query(value = "{'state' : { $in: [ 'PENDING', 'IN_PROGRESS' ] } }")
        ArrayList<Order> findActiveReservations();
    }