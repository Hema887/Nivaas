package com.juvarya.nivaas.access.mgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = { "com.juvarya.nivaas.service-partners"})
public class NivaasServicePartnersApp {

    public static void main(String[] args) {
        SpringApplication.run(NivaasServicePartnersApp.class, args);
    }

}
