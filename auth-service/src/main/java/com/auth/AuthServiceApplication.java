package com.auth;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("AUTH_DB_URL", dotenv.get("AUTH_DB_URL"));
		System.setProperty("AUTH_DB_USERNAME", dotenv.get("AUTH_DB_USERNAME"));
		System.setProperty("AUTH_DB_PASSWORD", dotenv.get("AUTH_DB_PASSWORD"));
		System.setProperty("EUREKA_SERVER", dotenv.get("EUREKA_SERVER"));

		SpringApplication.run(AuthServiceApplication.class, args);
	}
}
