package com.bookingservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Bookings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String email;
    private String mobile;
    private String propertyName;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String username;

    // Getters and setters
    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getPropertyName() { return propertyName; }
    public double getTotalPrice() { return totalPrice; }
    public BookingStatus getStatus() { return status; }
    public String getUsername() { return username; }

    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public void setUsername(String username) { this.username = username; }
}
