package com.payten.restapi.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestedReservationSlot {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer table;
}
