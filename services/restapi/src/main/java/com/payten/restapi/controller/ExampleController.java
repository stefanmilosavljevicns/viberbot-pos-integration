package com.payten.restapi.controller;

import com.payten.restapi.model.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ExampleController {
    @Autowired
    private SimpMessagingTemplate msgTemplate;
    @SendTo("/topic/greetings")
    @GetMapping("/exampleTest")
    public ResponseEntity<String> sendToTopic() {

        msgTemplate.convertAndSend("/topic/greetings", new Greeting("Update ready! Fetch new data"));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
