package com.payten.FoodRest.repository;

import com.payten.FoodRest.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepository extends MongoRepository<Users, String> {
}
