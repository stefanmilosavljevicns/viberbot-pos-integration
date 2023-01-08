package com.payten.FoodRest.controller;


import com.payten.FoodRest.model.Users;
import com.payten.FoodRest.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UsersController {
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/getAllUsers")
    //@RolesAllowed("admin")
    public ResponseEntity<List<Users>> findAll() {
        return ResponseEntity.ok(usersRepository.findAll());
    }
    @GetMapping("/getUser/{id}")
    //@RolesAllowed("admin")
    public ResponseEntity<Users> findById(@PathVariable(value = "id") String id){
        Optional<Users> userData = usersRepository.findById(id);
        return userData.map(users -> new ResponseEntity<>(users, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @GetMapping("/getLocation/{location}")
    public ResponseEntity<List<Users>> fetchLocation(@PathVariable(value = "location") String location){
        return ResponseEntity.ok(usersRepository.findByLocation(location));
    }

    @GetMapping("/getAllLocations")
    public ResponseEntity<List<String>> findAllLocations(){
        return ResponseEntity.ok(usersRepository.findDistinctLocations());
    }    
    @PostMapping("/addUser")
    //@RolesAllowed({"user", "admin"})
    public ResponseEntity<Users> save(@RequestBody Users users){
        users.setCreationDate(LocalDateTime.now());
        return ResponseEntity.ok(usersRepository.save(users));
    }
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") String id) {
        try {
            usersRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<Users> updateUsers(@PathVariable("id") String id, @RequestBody Users users) {
        Optional<Users> userData = usersRepository.findById(id);
        if (userData.isPresent()) {
            Users user = userData.get();
            user.setName(users.getName());
            user.setPin(users.getPin());
            user.setLastName(users.getLastName());
            user.setLocation(users.getLocation());
            user.setRole(users.getRole());
            user.setNickname(users.getNickname());
            user.setEmail(users.getEmail());
            return new ResponseEntity<>(usersRepository.save(user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
