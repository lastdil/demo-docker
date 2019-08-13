package com.cybernation.metrics.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
public class DemoController {

    @GetMapping("/demo")
    public String demo() {
        return UUID.randomUUID().toString();
    }
}
