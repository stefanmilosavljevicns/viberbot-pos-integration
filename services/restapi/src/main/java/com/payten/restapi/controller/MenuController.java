package com.payten.restapi.controller;

import com.payten.restapi.model.Menu;
import com.payten.restapi.repository.MenuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.payten.restapi.util.Constants.controllerLogFormat;

@RestController
@RequestMapping("${rest.path}")
public class MenuController {
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private MenuRepository menuRepository;

    @PostMapping("/addMenuItem")
    public ResponseEntity<Menu> save(@RequestBody Menu menu)
    {
        logger.info(String.format(controllerLogFormat, "addMenuItem", menu, HttpStatus.OK));
        return ResponseEntity.ok(menuRepository.save(menu));
    }


    @GetMapping("/getallCategories")
    public ResponseEntity<List<String>> findAllLocations() {
        logger.info(String.format(controllerLogFormat, "getallCategories", "", HttpStatus.OK));
        return ResponseEntity.ok(menuRepository.findDistinctCategories());
    }

    @GetMapping("/getItemByName/{name}")
    public ResponseEntity<Menu> fetchItemByName(@PathVariable(value = "name") String name) {
        logger.info(String.format(controllerLogFormat, "getItemByName", "", HttpStatus.OK));
        Menu menu = menuRepository.findByName(name);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/getCategoryItems/{category}")
    public ResponseEntity<List<Menu>> fetchLocation(@PathVariable(value = "category") String category) {
        logger.info(String.format(controllerLogFormat, "getCategoryItems", "", HttpStatus.OK));
        return ResponseEntity.ok(menuRepository.findByCategory(category));
    }
}
