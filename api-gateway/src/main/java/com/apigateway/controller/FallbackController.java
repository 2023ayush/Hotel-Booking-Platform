package com.apigateway.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public Mono<String> authFallback() {
        return Mono.just("Auth service is temporarily unavailable");
    }

    @GetMapping("/booking")
    public Mono<String> bookingFallback() {
        return Mono.just("Booking service is temporarily unavailable");
    }

    @GetMapping("/property")
    public Mono<String> propertyFallback() {
        return Mono.just("Property service is temporarily unavailable");
    }
}
