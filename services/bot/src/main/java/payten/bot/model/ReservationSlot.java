package payten.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSlot {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer table;
}
