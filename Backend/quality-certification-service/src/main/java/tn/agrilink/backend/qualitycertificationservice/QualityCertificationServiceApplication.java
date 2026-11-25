package tn.agrilink.backend.qualitycertificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient //facultatif
@SpringBootApplication
public class QualityCertificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QualityCertificationServiceApplication.class, args);
    }

}
