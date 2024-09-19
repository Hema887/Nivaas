package com.juvarya.nivaas.access.mgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = { "com.juvarya.nivaas.access.mgmt", "com.juvarya.nivaas.auth" })
public class AccessManagementApp {

    public static void main(String[] args) {
        SpringApplication.run(AccessManagementApp.class, args);
    }

}
