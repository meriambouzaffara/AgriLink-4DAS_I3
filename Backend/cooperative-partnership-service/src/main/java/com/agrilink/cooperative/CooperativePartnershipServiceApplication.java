package com.agrilink.cooperative;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CooperativePartnershipServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CooperativePartnershipServiceApplication.class, args);
    }
}
