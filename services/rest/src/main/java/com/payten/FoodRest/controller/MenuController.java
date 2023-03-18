package com.payten.FoodRest.controller;

import com.payten.FoodRest.model.Menu;
import com.payten.FoodRest.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MenuController {
    @Autowired
    private MenuRepository menuRepository;

    @PostMapping("/addMenuItem")
    public ResponseEntity<Menu> save(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuRepository.save(menu));
    }

    @GetMapping("/getWholeMenu")
    public ResponseEntity<List<Menu>> findAll() {
        return ResponseEntity.ok(menuRepository.findAll());
    }

    @GetMapping("/getallCategories")
    public ResponseEntity<List<String>> findAllLocations() {
        return ResponseEntity.ok(menuRepository.findDistinctCategories());
    }

    @GetMapping("/getPriceByName/{name}")
    public ResponseEntity<Double> fetchPriceByName(@PathVariable(value = "name") String name) {
        Menu menu = menuRepository.findPriceByName(name);
        return ResponseEntity.ok(menu.getPrice());
    }

    @GetMapping("/getCategoryItems/{category}")
    public ResponseEntity<List<Menu>> fetchLocation(@PathVariable(value = "category") String category) {
        return ResponseEntity.ok(menuRepository.findByCategory(category));
    }
}
