# 🏨 Hotel Booking System - Microservices Architecture

This is a **Hotel Booking System** built using **Spring Boot Microservices** with **JWT-based authentication**, **AWS S3 image storage**, and **Spring Cloud Gateway** for routing and security. The system is designed to be scalable, secure, and easy to extend.

---

## 📌 Features

### 🔐 Authentication & API Gateway
- JWT-based login and signup
- Centralized routing using Spring Cloud Gateway
- Secure access to internal services

### 🏢 Property Service
- Add hotels or properties
- Upload and store room images on **AWS S3**
- Property CRUD APIs (Create, Read, Update, Delete)

### 📅 Booking Service
- Check room availability
- Book rooms for selected dates
- Prevent double booking (date validation logic)

### 🌐 Microservice Communication
- Uses **Feign Clients** for clean and type-safe REST communication between services

---

## 🧱 Tech Stack

| Layer              | Tools / Technologies                                 |
|--------------------|------------------------------------------------------|
| Language           | Java 17                                              |
| Backend Framework  | Spring Boot                                          |
| API Gateway        | Spring Cloud Gateway                                 |
| Service Discovery  | Spring Cloud Eureka                                  |
| Security           | Spring Security + JWT                                |
| Database           | MySQL                                                |
| Storage            | AWS S3                                               |
| Communication      | Feign Client                                         |
| Build Tool         | Maven                                                |
| Containerization   | Docker                                               |

---

## 📂 Project Structure

```bash
hotel-booking-system/
├── api-gateway/
├── service-registry/
├── auth-service/
├── property-service/
├── booking-service/
└── common-utils/ (optional DTOs/constants)
