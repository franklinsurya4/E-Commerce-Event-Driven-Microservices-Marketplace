package com.example.order_services.Controller;

import com.example.order_services.DTO.OrderRequest;
import com.example.order_services.Model.Order;
import com.example.order_services.Services.OrderServices;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Slf4j
public class OrderController {

    private final OrderServices orderServices;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    public CompletableFuture<ResponseEntity<Map<String, String>>> placeOrder(
            @RequestBody OrderRequest orderRequest) {

        return CompletableFuture.supplyAsync(() -> {
            String result = orderServices.placeOrder(orderRequest);
            return ResponseEntity.ok(Map.of("message", result));
        });
    }


    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderServices.getAllOrders());
    }

    public CompletableFuture<ResponseEntity<Map<String, Object>>> fallbackMethod(
            OrderRequest request, Throwable ex) {

        return CompletableFuture.completedFuture(
                ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of(
                                "status", "FAILED",
                                "reason", "Inventory service timeout"
                        ))
        );
    }


}


