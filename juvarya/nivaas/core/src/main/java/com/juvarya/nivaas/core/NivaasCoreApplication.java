package com.juvarya.nivaas.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = { "com.juvarya.nivaas.core", "com.juvarya.nivaas.auth" })
public class NivaasCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(NivaasCoreApplication.class, args);
	}

}
