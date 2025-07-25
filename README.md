# 🏨 Hotel Booking System - Microservices Architecture

This is a **Hotel Booking System** built using **Spring Boot Microservices**, featuring **JWT-based authentication**, **Kafka-based asynchronous notifications**, **AWS S3 image storage**, and **Spring Cloud Gateway** for centralized routing and security.

The system is designed for real-world scalability, modularity, and cloud readiness.

---

## 📚 API Documentation

Explore the complete Postman API collection here:  
👉 [Hotel Booking System API Docs](https://documenter.getpostman.com/view/33677881/2sB34kDeEU)

---

## 📌 Features

### 🔐 Authentication & API Gateway
- User Registration & Login with **JWT Authentication**
- API access control via **Spring Cloud Gateway**
- Role-based authorization

### 🏢 Property Service
- Add hotel/property details
- Upload and store images using **AWS S3**
- Full property CRUD (Create, Read, Update, Delete)
- **Asynchronously sends email** using Kafka after property is added

### 📅 Booking Service
- Search and book available rooms
- Prevent double bookings with **date conflict validation**
- **Asynchronously sends email** via Kafka after booking confirmation

### 📣 Notification Service
- Consumes messages from Kafka
- Sends **emails using JavaMailSender**
- (Optional) Can be extended to send **SMS using Twilio or Nexmo**

### 🌐 Microservice Communication
- Uses **OpenFeign clients** for internal REST communication between services
- Registered via **Spring Cloud Eureka (Service Registry)**

---

## 🧱 Tech Stack

| Layer              | Tools / Technologies                                 |
|--------------------|------------------------------------------------------|
| Language           | Java 17                                              |
| Backend Framework  | Spring Boot                                          |
| API Gateway        | Spring Cloud Gateway                                 |
| Service Discovery  | Spring Cloud Eureka                                  |
| Security           | Spring Security + JWT                                |
| Asynchronous Comm. | Apache Kafka                                         |
| Email Sender       | JavaMailSender                                       |
| Database           | MySQL                                                |
| Storage            | AWS S3                                               |
| REST Communication | OpenFeign                                            |
| Build Tool         | Maven                                                |
| Containerization   | Docker                                               |

---

## 🧭 Kafka Message Flow

| Trigger Event         | Kafka Producer Service | Kafka Topic   | Kafka Consumer Service | Action Taken              |
|-----------------------|------------------------|----------------|-------------------------|---------------------------|
| Property is added     | `property-service`      | `send_email`   | `notification-service`  | Sends confirmation email |
| Booking is completed  | `booking-service`       | `send_email`   | `notification-service`  | Sends booking email      |
