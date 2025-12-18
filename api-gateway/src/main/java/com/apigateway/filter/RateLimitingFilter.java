package com.apigateway.filter;

import com.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;

    private static final int MAX_REQUESTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    public RateLimitingFilter(@Qualifier("reactiveRedisTemplate") ReactiveStringRedisTemplate redisTemplate,
                              JwtUtil jwtUtil) {
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        String redisKey;
        String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

        // 🔹 CASE 1: LOGIN API → IP-based rate limit
        if (path.startsWith("/auth")) {
            redisKey = "rate_limit:login:ip:" + clientIp;
        }
        // 🔹 CASE 2: Protected APIs → JWT-based rate limit
        else {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // optional: rate limit public APIs by IP
                redisKey = "rate_limit:ip:" + clientIp;
            } else {
                String token = authHeader.substring(7);
                String username;
                try {
                    username = jwtUtil.extractUsername(token);
                } catch (Exception e) {
                    // fallback: rate limit by IP if JWT invalid
                    redisKey = "rate_limit:ip:" + clientIp;
                    System.out.println("JWT invalid, fallback to IP rate limiting: " + redisKey);
                    return incrementAndCheck(exchange, chain, redisKey, path);
                }
                redisKey = "rate_limit:user:" + username;
            }
        }

        System.out.println("Rate limiting key: " + redisKey);
        return incrementAndCheck(exchange, chain, redisKey, path);
    }

    private Mono<Void> incrementAndCheck(ServerWebExchange exchange, GatewayFilterChain chain,
                                         String redisKey, String path) {

        return redisTemplate.opsForValue().increment(redisKey)
                .flatMap(count -> {
                    if (count == 1) {
                        redisTemplate.expire(redisKey, WINDOW).subscribe();
                    }

                    if (count > MAX_REQUESTS) {
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

                        String json = "{ \"status\": 429, \"error\": \"Too Many Requests\", " +
                                "\"message\": \"You have exceeded the maximum number of requests.\", " +
                                "\"path\": \"" + path + "\" }";

                        DataBuffer buffer = exchange.getResponse()
                                .bufferFactory()
                                .wrap(json.getBytes(StandardCharsets.UTF_8));

                        return exchange.getResponse().writeWith(Mono.just(buffer));
                    }

                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
