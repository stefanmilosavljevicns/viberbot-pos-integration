package com.payten.FoodRest.repository;
import java.time.LocalDateTime;
import java.util.List;

import com.payten.FoodRest.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

    public interface OrderRepository extends MongoRepository<Order, String> {
        List<Order> findByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime end, LocalDateTime start);
        List<Order> findByStartTimeGreaterThan(LocalDateTime twentyFourHoursAgo);
        @Query(value = "{ 'viberID' : ?0, 'startTime' : ?1 }")
        Order findByViberId(String name, LocalDateTime startTime);

    }