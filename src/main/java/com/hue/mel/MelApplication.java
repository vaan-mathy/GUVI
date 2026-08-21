package com.hue.mel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // MAGIC LINE: Wakes up the background multi-threading engine for Java 17
public class MelApplication {

	public static void main(String[] args) {
		SpringApplication.run(MelApplication.class, args);
	}
}
