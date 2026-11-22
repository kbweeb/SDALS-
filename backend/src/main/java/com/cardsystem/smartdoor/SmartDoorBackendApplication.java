package com.cardsystem.smartdoor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SmartDoorBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartDoorBackendApplication.class, args);
	}

}
