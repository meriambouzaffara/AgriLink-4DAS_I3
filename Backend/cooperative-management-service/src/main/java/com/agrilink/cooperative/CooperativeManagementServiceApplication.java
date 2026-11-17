package com.agrilink.cooperative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CooperativeManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CooperativeManagementServiceApplication.class, args);
    }
}
