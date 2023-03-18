package com.payten.FoodRest.repository;

import com.payten.FoodRest.model.Customers;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CustomersRepository extends MongoRepository<Customers, String> {

}
