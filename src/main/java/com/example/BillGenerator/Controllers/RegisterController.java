package com.example.BillGenerator.Controllers;

import com.example.BillGenerator.Dto.RegisterUserDto;
import com.example.BillGenerator.Dto.UserRegisteredPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class RegisterController {
    static String QUEUE_NAME = "user-details";

    private final RabbitTemplate rabbitTemplate;

    public RegisterController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/registers")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody RegisterUserDto registerUserDto) throws JsonProcessingException {
        // TODO save user in the database

        Random random = new Random();
        int confirmationCode = random.nextInt(900000) + 100000;

        UserRegisteredPayload queuePayload = new UserRegisteredPayload(
                registerUserDto.name(),
                registerUserDto.email(),
                confirmationCode
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String queuePayloadString = objectMapper.writeValueAsString(queuePayload);

        rabbitTemplate.convertAndSend(QUEUE_NAME, queuePayloadString);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully!");

        return ResponseEntity.ok(response);
    }
}
