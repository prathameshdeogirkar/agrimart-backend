package com.agrimart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgrimartBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgrimartBackendApplication.class, args);
        System.out.println("Agrimart Backend Application is running...");
    }
}
