package com.jtudy.education;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EducationApplication  {

	public static void main(String[] args) {
		SpringApplication.run(EducationApplication.class, args);
	}

}
