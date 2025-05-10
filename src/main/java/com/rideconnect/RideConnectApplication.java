package com.rideconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.rideconnect")
@EnableScheduling
@EnableJpaAuditing
@EntityScan(basePackages = "com.rideconnect.entity")
@EnableJpaRepositories(basePackages = "com.rideconnect.repository")
public class RideConnectApplication {
	public static void main(String[] args) {
		SpringApplication.run(RideConnectApplication.class, args);
	}
}
