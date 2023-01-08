package com.payten.FoodRest.repository;
import com.payten.FoodRest.model.Reservations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ReservationsRepository extends MongoRepository<Reservations, String> {
    @Query(value = "{'reservation' : ?0}", delete = true)
    void deleteByTime(Float time);
}