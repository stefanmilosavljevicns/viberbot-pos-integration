package com.payten.FoodRest.repository;
import java.time.LocalDateTime;
import java.util.List;
import com.payten.FoodRest.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;

    public interface OrderRepository extends MongoRepository<Order, String> {
        List<Order> findByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime end, LocalDateTime start);

    }