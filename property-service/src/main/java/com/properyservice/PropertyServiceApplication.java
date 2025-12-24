package com.properyservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PropertyServiceApplication {

	public static void main(String[] args) {
		// Load .env file
		Dotenv dotenv = Dotenv.load();

		// Database
		System.setProperty("PROPERTY_DB_URL", dotenv.get("PROPERTY_DB_URL"));
		System.setProperty("PROPERTY_DB_USERNAME", dotenv.get("PROPERTY_DB_USERNAME"));
		System.setProperty("PROPERTY_DB_PASSWORD", dotenv.get("PROPERTY_DB_PASSWORD"));

		// Redis
		System.setProperty("REDIS_HOST", dotenv.get("REDIS_HOST"));
		System.setProperty("REDIS_PORT", dotenv.get("REDIS_PORT"));

		// Kafka
		System.setProperty("KAFKA_BOOTSTRAP_SERVERS", dotenv.get("KAFKA_BOOTSTRAP_SERVERS"));

		// AWS S3
		System.setProperty("AWS_ACCESS_KEY", dotenv.get("AWS_ACCESS_KEY"));
		System.setProperty("AWS_SECRET_KEY", dotenv.get("AWS_SECRET_KEY"));

		// Eureka
		System.setProperty("EUREKA_SERVER", dotenv.get("EUREKA_SERVER"));

		// Run Spring Boot
		SpringApplication.run(PropertyServiceApplication.class, args);
	}
}
