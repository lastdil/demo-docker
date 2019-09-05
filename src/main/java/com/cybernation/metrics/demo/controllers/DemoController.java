package com.cybernation.metrics.demo.controllers;

import com.cybernation.metrics.demo.service.FactorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;

@RestController
public class DemoController {
    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    FactorialService factorialService;

    @GetMapping("/demo")
    public String demo() {
        return UUID.randomUUID().toString();
    }

    @GetMapping("/test")
    public int factorial() {
        Random r = new Random();
        int n = r.nextInt((50 - 5) + 1) + 5;
        return factorialService.calculateFactorial(n);
    }
}