package org.capstone.nursery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NurseryApplication {

	public static void main(String[] args) {
		SpringApplication.run(NurseryApplication.class, args);
	}

}
