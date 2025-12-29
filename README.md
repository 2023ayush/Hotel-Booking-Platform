# 🏨 Hotel Management System (Microservices Architecture)

A **Spring Boot–based Hotel Management System** built using **microservices architecture**, focusing on scalability, resilience, and real‑world production practices. This project demonstrates service discovery, centralized configuration, API gateway routing, circuit breakers, Dockerization, and clean layered architecture.

[![Watch the demo](https://img.youtube.com/vi/LtZOS5SmiKo/0.jpg)](https://www.youtube.com/watch?v=LtZOS5SmiKo&t=28s)

---

## 🚀 Tech Stack

* **Backend**: Java, Spring Boot, Spring Cloud
* **Microservices**: Auth Service, Booking Service, Property Service
* **API Gateway**: Spring Cloud Gateway
* **Service Discovery**: Netflix Eureka
* **Config Management**: Spring Cloud Config Server (Git backend)
* **Resilience**: Circuit Breaker with fallback (Resilience4j)
* **Database**: MySQL (AWS RDS)
* **Caching**: Redis
* **Containerization**: Docker & Docker Compose
* **Build Tool**: Maven

---

## 🧩 Microservices Overview

### 🔐 Auth Service

* User authentication and authorization
* Centralized exception handling
* Clean controller–service separation
* Integrated with Config Server
* Dockerized for production

### 📅 Booking Service

* Room booking and availability logic
* DTO-based request/response handling
* Validation for total nights vs booking dates
* Circuit breaker with fallback handling
* Client fixes and test coverage
* Docker + Config Server integration

### 🏢 Property Service

* Property and room management
* Refactored controllers, services, and DTOs
* Improved business logic clarity
* Centralized exception handling

### 🌐 API Gateway

* Single entry point for all services
* Dynamic routing via Eureka discovery
* Circuit breaker with fallback responses
* Redis integration
* Dockerized with Config Server support

---

## ⚙️ Configuration Server

* Centralized configuration using **Spring Cloud Config Server**
* Git-backed configuration repository
* Environment-based configs (dev / prod)
* `.env` files ignored for security
* Production-ready `application.yml`

---

## 🛡 Resilience & Fault Tolerance

* Implemented **Circuit Breaker pattern**
* Fallback responses for:

  * API Gateway
  * Booking Service
* Prevents cascading failures during service downtime

---

---

## 📊 Monitoring

### Prometheus
![Prometheus Screenshot](path/to/prometheus.png)

### Grafana
![Grafana Screenshot](path/to/grafana.png)



## 🐳 Docker Support

Each major component is Dockerized:

* Config Server
* API Gateway
* Auth Service
* Booking Service
* Property Service

---

## 📂 Project Structure (High Level)

```
hotel-management-system
│
├── config-server
├── api-gateway
├── auth-service
├── booking-service
├── property-service
├── docker-compose.yml
└── README.md
```

---

## 🧪 Code Quality & Practices

* Clean layered architecture (Controller → Service → Repository)
* DTO-based API design
* Centralized exception handling
* Refactored services for readability and maintainability
* IDE and build artifacts ignored via `.gitignore`

---


## 🎯 Project Goals

* Practice **real-world microservices architecture**
* Learn **Spring Cloud ecosystem**
* Implement **fault tolerance and centralized config**
* Gain hands-on experience with **Docker & AWS RDS**

---
