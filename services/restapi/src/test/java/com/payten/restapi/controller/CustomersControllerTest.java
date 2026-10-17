package com.payten.restapi.controller;

import com.payten.restapi.repository.CustomersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomersControllerTest {
    private CustomersRepository repository;
    private CustomersController controller;

    @BeforeEach
    void setUp() {
        repository = mock(CustomersRepository.class);
        controller = new CustomersController();
        ReflectionTestUtils.setField(controller, "customersRepository", repository);
        when(repository.findById("unknown")).thenReturn(Optional.empty());
    }

    @Test
    void missingCustomerHasAnEmptyCartAndZeroTotals() {
        assertEquals(false, controller.checkIfCartIsEmpty("unknown").getBody());
        assertEquals(0, controller.getTotalTime("unknown").getBody());
        assertEquals(0.0, controller.getTotalPrice("unknown").getBody());
        assertEquals(0, controller.getCart("unknown").getBody().size());
    }

    @Test
    void cartMutationsReturnNotFoundForMissingCustomer() {
        ResponseEntity<?> removeResult = controller.removeItemFromCart("unknown", null);
        ResponseEntity<?> clearResult = controller.clearCart("unknown");

        assertEquals(HttpStatus.NOT_FOUND, removeResult.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, clearResult.getStatusCode());
    }
}
