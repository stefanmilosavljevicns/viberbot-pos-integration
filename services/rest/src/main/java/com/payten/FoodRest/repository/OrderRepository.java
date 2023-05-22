package com.payten.FoodRest.repository;
import java.time.LocalDateTime;
import java.util.List;

import com.payten.FoodRest.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

    public interface OrderRepository extends MongoRepository<Order, String> {
        @Query(value = "{ 'startTime' > ?0, 'startTime' < ?1 }")
        List<Order> findUsersToRemindForReservation(LocalDateTime start, LocalDateTime end);          
        List<Order> findByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime end, LocalDateTime start);
        List<Order> findByStartTimeGreaterThan(LocalDateTime twentyFourHoursAgo);
        @Query(value = "{ 'viberID' : ?0, 'state' : { $nin: [ 'COMPLETED', 'DECLINED' ] } }")
        Order findByViberId(String name);

    }