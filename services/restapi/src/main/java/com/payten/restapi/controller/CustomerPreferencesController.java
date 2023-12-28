package com.payten.restapi.controller;

import com.payten.restapi.model.CustomerLocale;
import com.payten.restapi.model.CustomerPreferences;
import com.payten.restapi.repository.CustomerPreferencesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("${rest.path}")
public class CustomerPreferencesController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    CustomerPreferencesRepository customerPreferencesRepository;

    @GetMapping("/getUserLocale/{viberId}")
        public Optional<CustomerPreferences> getUserLocale(@PathVariable("viberId") String viberId){
            Optional<CustomerPreferences> customerPreferences = customerPreferencesRepository.findById(viberId);
            if(customerPreferences.isEmpty()) {
                customerPreferences = Optional.of(new CustomerPreferences(viberId, CustomerLocale.SRP));
                customerPreferencesRepository.save(customerPreferences.get());
            }
            return customerPreferences;
        }
    }

