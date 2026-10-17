package com.payten.restapi.controller;

import com.payten.restapi.model.Order;
import com.payten.restapi.model.OrderState;
import com.payten.restapi.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderControllerTest {
    private OrderRepository orderRepository;
    private OrderController controller;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        controller = new OrderController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "orderRepository", orderRepository);
    }

    @Test
    void returnsOnlyWorkingDaySlotsThatDoNotOverlapActiveOrders() {
        LocalDate day = LocalDate.of(2026, 10, 19);
        Order conflict = new Order("1", new ArrayList<>(List.of("service")), 1.0,
                day.atTime(8, 30), day.atTime(9, 30), OrderState.PENDING, "customer");
        when(orderRepository.findByStartTimeLessThanAndEndTimeGreaterThanAndStateNotIn(
                eq(day.atTime(17, 0)), eq(day.atTime(7, 0)), any())).thenReturn(List.of(conflict));

        List<LocalDateTime> result = controller.getDateTimes(day, 60);

        assertEquals(List.of(day.atTime(7, 0), day.atTime(10, 0), day.atTime(11, 0), day.atTime(12, 0),
                day.atTime(13, 0), day.atTime(14, 0), day.atTime(15, 0), day.atTime(16, 0)), result);
    }

    @Test
    void rejectsNonPositiveReservationDuration() {
        assertThrows(ResponseStatusException.class,
                () -> controller.getDateTimes(LocalDate.of(2026, 10, 19), 0));
    }
}
