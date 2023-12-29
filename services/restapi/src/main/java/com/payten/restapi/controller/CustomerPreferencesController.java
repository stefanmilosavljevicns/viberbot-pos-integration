package com.payten.restapi.controller;

import com.payten.restapi.model.CustomerLocale;
import com.payten.restapi.model.CustomerPreferences;
import com.payten.restapi.model.Order;
import com.payten.restapi.model.OrderState;
import com.payten.restapi.repository.CustomerPreferencesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.payten.restapi.util.Constants.controllerLogFormat;

@RestController
@RequestMapping("${rest.path}")
public class CustomerPreferencesController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerPreferencesController.class);

    @Autowired
    CustomerPreferencesRepository customerPreferencesRepository;

    @GetMapping("/getUserLocale")
        public Optional<CustomerPreferences> getUserLocale(@RequestParam("viberId") String viberId){
            Optional<CustomerPreferences> customerPreferences = customerPreferencesRepository.findById(viberId);
            if(customerPreferences.isEmpty()) {
                customerPreferences = Optional.of(new CustomerPreferences(viberId, CustomerLocale.SRB));
                customerPreferencesRepository.save(customerPreferences.get());
            }
            return customerPreferences;
        }
    @PutMapping("/changeLocale")
    public ResponseEntity<CustomerPreferences> changeLocale(@RequestParam("viberId") String viberId,@RequestParam("locale") CustomerLocale locale) {
        Optional<CustomerPreferences> existingOrder = customerPreferencesRepository.findById(viberId);
        existingOrder.get().setCustomerLocale(locale);
        logger.info(String.format(controllerLogFormat, "changeLocale",  HttpStatus.OK));
        return new ResponseEntity<>(customerPreferencesRepository.save(existingOrder.get()), HttpStatus.OK);
    }
    }

