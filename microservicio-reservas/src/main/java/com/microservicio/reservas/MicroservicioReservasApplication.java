package com.microservicio.reservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroservicioReservasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioReservasApplication.class, args);
	}

}
