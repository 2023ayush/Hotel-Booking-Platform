package com.bookingservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookingServiceApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("BOOKING_DB_URL", dotenv.get("BOOKING_DB_URL"));
		System.setProperty("BOOKING_DB_USERNAME", dotenv.get("BOOKING_DB_USERNAME"));
		System.setProperty("BOOKING_DB_PASSWORD", dotenv.get("BOOKING_DB_PASSWORD"));
		System.setProperty("EUREKA_SERVER", dotenv.get("EUREKA_SERVER"));

		SpringApplication.run(BookingServiceApplication.class, args);
	}
}
