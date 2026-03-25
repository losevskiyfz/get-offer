package com.github.losevskiyfz.calculationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CalculationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalculationServiceApplication.class, args);
    }

}
