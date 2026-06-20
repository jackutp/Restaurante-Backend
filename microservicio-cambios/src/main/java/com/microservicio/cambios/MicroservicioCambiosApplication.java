package com.microservicio.cambios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroservicioCambiosApplication {
	public static void main(String[] args) {
		SpringApplication.run(MicroservicioCambiosApplication.class, args);
	}
}