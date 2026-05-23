package com.microservicio.Proveedor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroservicioProveedorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioProveedorApplication.class, args);
	}

}
