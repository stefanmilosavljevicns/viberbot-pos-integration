package com.payten.FoodRest.repository;

import com.payten.FoodRest.model.Customers;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomersRepository extends MongoRepository<Customers, String> {
}
