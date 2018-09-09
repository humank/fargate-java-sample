package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class PetClinicApplication {

    @Configuration
    @ComponentScan(lazyInit = true)
    class MyConfiguration{

    }
	public static void main(String[] args) {
		SpringApplication.run(PetClinicApplication.class, args);
	}
}
