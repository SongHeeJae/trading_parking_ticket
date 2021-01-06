package com.kuke.parkingticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ParkingticketApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingticketApplication.class, args);
	}

}
