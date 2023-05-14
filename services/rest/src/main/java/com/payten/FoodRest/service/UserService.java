package com.payten.FoodRest.service;

import com.payten.FoodRest.dto.UserRequest;
import com.payten.FoodRest.model.User;

import java.util.List;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll ();
	User save(UserRequest userRequest);
}
