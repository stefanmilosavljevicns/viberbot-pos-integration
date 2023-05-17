package com.payten.FoodRest.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.payten.FoodRest.model.Role;

import java.util.List;

public interface RoleRepository extends MongoRepository<Role, Long> {
    List<Role> findByName(String name);
}
