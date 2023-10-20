package com.payten.restapi.controller;

import com.payten.restapi.model.Menu;
import com.payten.restapi.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${rest.path}")
public class MenuController {
    @Autowired
    private MenuRepository menuRepository;

    @PostMapping("/addMenuItem")
    public ResponseEntity<Menu> save(@RequestBody Menu menu)
    {
        return ResponseEntity.ok(menuRepository.save(menu));
    }


    @GetMapping("/getallCategories")
    public ResponseEntity<List<String>> findAllLocations() {
        return ResponseEntity.ok(menuRepository.findDistinctCategories());
    }

    @GetMapping("/getItemByName/{name}")
    public ResponseEntity<Menu> fetchItemByName(@PathVariable(value = "name") String name) {
        Menu menu = menuRepository.findByName(name);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/getCategoryItems/{category}")
    public ResponseEntity<List<Menu>> fetchLocation(@PathVariable(value = "category") String category) {
        return ResponseEntity.ok(menuRepository.findByCategory(category));
    }
}
