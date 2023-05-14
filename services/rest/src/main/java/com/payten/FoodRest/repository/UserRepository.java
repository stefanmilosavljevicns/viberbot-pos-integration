package com.payten.FoodRest.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.payten.FoodRest.model.User;

public interface UserRepository extends MongoRepository<User, Long> {
    User findByUsername(String username);
}
