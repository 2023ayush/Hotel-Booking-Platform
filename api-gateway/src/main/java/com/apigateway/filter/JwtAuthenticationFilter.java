package com.apigateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String SECRET_KEY = "secret12345";

    // ================= PUBLIC ENDPOINTS =================
// These endpoints are accessible WITHOUT JWT authentication.
// Used for login, registration, and other open APIs.
    private static final List<String> PUBLIC_ENDPOINTS = List.of(

            // -------- AUTH SERVICE (No token required) --------

            "/auth/api/v1/auth/login/user",      // User login endpoint
            "/auth/api/v1/auth/login/admin",     // Admin login endpoint
            "/auth/api/v1/auth/register",        // User registration endpoint
            "/auth/api/v1/auth/update-password", // Forgot / update password endpoint

            // -------- PROPERTY SERVICE (Public access) --------

            "/property/api/v1/properties/search-paged" // Public property search (paginated)
    );

    // ================= PROTECTED ENDPOINTS =================
// Endpoints that require JWT authentication mapped to roles
    private static final Map<String, List<String>> PROTECTED_ENDPOINTS_WITH_ROLES = new LinkedHashMap<>();

    static {
// ================= BOOKING SERVICE =================
        PROTECTED_ENDPOINTS_WITH_ROLES.put("/booking/api/v1/booking/add-to-cart", List.of("ROLE_USER", "ROLE_ADMIN")); // Add booking
        PROTECTED_ENDPOINTS_WITH_ROLES.put("/booking/api/v1/booking/checkout", List.of("ROLE_USER")); // Checkout
        PROTECTED_ENDPOINTS_WITH_ROLES.put("/booking/api/v1/booking/list", List.of("ROLE_USER", "ROLE_ADMIN")); // List bookings (user/admin)
        PROTECTED_ENDPOINTS_WITH_ROLES.put("/booking/api/v1/booking/cancel/*", List.of("ROLE_USER")); // Cancel booking by ID
        PROTECTED_ENDPOINTS_WITH_ROLES.put("/booking/api/v1/booking/admin/all", List.of("ROLE_ADMIN")); // Admin view all bookings

        // Property Service (all admin endpoints)
        PROTECTED_ENDPOINTS_WITH_ROLES.put("/property/api/v1/properties", List.of("ROLE_ADMIN")); // Add, list, update, delete
        PROTECTED_ENDPOINTS_WITH_ROLES.put("/property/api/v1/properties/", List.of("ROLE_ADMIN")); // For ID-based endpoints
        PROTECTED_ENDPOINTS_WITH_ROLES.put("/property/api/v1/properties/rooms", List.of("ROLE_ADMIN"));
        PROTECTED_ENDPOINTS_WITH_ROLES.put("/property/api/v1/properties/rooms/", List.of("ROLE_ADMIN"));
        PROTECTED_ENDPOINTS_WITH_ROLES.put("/property/api/v1/properties/rooms/*/availability", List.of("ROLE_ADMIN", "ROLE_USER"));

    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();

        // 1️⃣ Skip JWT check for public endpoints
        if (isPublicEndpoint(requestPath)) {
            return chain.filter(exchange);
        }

        // 2️⃣ Check Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        try {
            // 3️⃣ Validate JWT
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);

            String role = jwt.getClaim("role").asString();
            String username = jwt.getSubject();

            // 4️⃣ Check role authorization
            if (!isAuthorized(requestPath, role)) {
                return forbiddenResponse(exchange, "You do not have permission to access this resource");
            }

            // 5️⃣ Forward role and username downstream
            exchange = exchange.mutate()
                    .request(r -> r.header("X-User-Role", role)
                            .header("X-User-Name", username))
                    .build();

        } catch (JWTVerificationException e) {
            return unauthorizedResponse(exchange, "Invalid or expired token");
        }

        // 6️⃣ Continue filter chain
        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private boolean isAuthorized(String path, String role) {
        for (Map.Entry<String, List<String>> entry : PROTECTED_ENDPOINTS_WITH_ROLES.entrySet()) {
            String protectedPath = entry.getKey();
            List<String> allowedRoles = entry.getValue();
            if (path.startsWith(protectedPath)) {
                return allowedRoles.contains(role);
            }
        }
        // If not listed, default to deny
        return false;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"status\":401,\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(bytes)));
    }

    private Mono<Void> forbiddenResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"status\":403,\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
