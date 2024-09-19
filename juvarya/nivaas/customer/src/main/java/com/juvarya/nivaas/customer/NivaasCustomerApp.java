package com.juvarya.nivaas.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
@EnableFeignClients
@ComponentScan(basePackages = { "com.juvarya.nivaas.customer", "com.juvarya.nivaas.auth" })
public class NivaasCustomerApp {

	public static void main(String[] args) {
		SpringApplication.run(NivaasCustomerApp.class, args);
	}

}
