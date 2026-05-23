package com.microservicio.mesas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroservicioMesasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioMesasApplication.class, args);
	}

}
