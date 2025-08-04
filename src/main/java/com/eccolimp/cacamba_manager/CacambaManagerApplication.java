package com.eccolimp.cacamba_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CacambaManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacambaManagerApplication.class, args);
	}

}
