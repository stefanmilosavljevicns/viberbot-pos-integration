package com.payten.restapi.repository;

import com.payten.restapi.model.CustomerPreferences;
import com.payten.restapi.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerPreferencesRepository extends MongoRepository<CustomerPreferences, String> {
}
