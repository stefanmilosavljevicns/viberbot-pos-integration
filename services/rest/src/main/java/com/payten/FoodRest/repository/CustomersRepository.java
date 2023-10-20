package com.payten.restapi.repository;

import com.payten.restapi.model.Customers;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CustomersRepository extends MongoRepository<Customers, String> {

}
