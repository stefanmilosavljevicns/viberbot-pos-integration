package com.payten.FoodRest.controller;
import com.payten.FoodRest.model.Reservations;
import com.payten.FoodRest.repository.ReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ReservationsController {
    @Autowired
    private ReservationsRepository reservationsRepository;
    @PostMapping("/addReservation")
    //@RolesAllowed({"user", "admin"})
    public ResponseEntity<Reservations> save(@RequestBody Reservations reservations){
        return ResponseEntity.ok(reservationsRepository.save(reservations));
    }
    @GetMapping("/getAllReservations")
    //@RolesAllowed("admin")
    public ResponseEntity<List<Reservations>> findAll(){
        return ResponseEntity.ok(reservationsRepository.findAll());
    }
    @DeleteMapping("/deleteTime/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Float id) {
        try {
            reservationsRepository.deleteByTime(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
