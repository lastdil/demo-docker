package com.cybernation.metrics.demo.service;

import org.springframework.stereotype.Service;

@Service
public class FactorialService {
    public int calculateFactorial(int n) {
        int result = 1;
        for (int i = 1; i <= n; i++) {
            result = result * i;
        }
        return result;
    }
//
}
