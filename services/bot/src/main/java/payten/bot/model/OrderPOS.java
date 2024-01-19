package payten.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPOS {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String state;
    private String customerName;
    private Integer tableNumber;
    private String viberID;

    public OrderPOS(LocalDateTime startTime, LocalDateTime endTime, String state, String customerName, String viberID, Integer tableNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
        this.customerName = customerName;
        this.viberID = viberID;
        this.tableNumber = tableNumber;
    }
}