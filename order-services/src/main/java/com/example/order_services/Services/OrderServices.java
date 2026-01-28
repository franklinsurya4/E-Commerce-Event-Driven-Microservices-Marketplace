package com.example.order_services.Services;

import com.example.order_services.DTO.*;
import com.example.order_services.Model.*;
import com.example.order_services.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServices {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest request) {

        if (request == null || request.getOrderLineItemsDtoList() == null
                || request.getOrderLineItemsDtoList().isEmpty()) {
            return "Invalid order request";
        }

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> items = request.getOrderLineItemsDtoList()
                .stream()
                .map(dto -> {
                    OrderLineItems i = new OrderLineItems();
                    i.setSkuCode(dto.getSkuCode());
                    i.setPrice(dto.getPrice());
                    i.setQuantity(dto.getQuantity());
                    return i;
                })
                .toList();

        order.setOrderLineItemsList(items);

        List<String> skuCodes = items.stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] response = webClientBuilder.build()
                .get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        if (response == null || response.length == 0) {
            return "Inventory service unavailable";
        }

        boolean allInStock = Arrays.stream(response)
                .allMatch(InventoryResponse::isInStock);

        if (!allInStock) {
            return "Product out of stock";
        }

        orderRepository.save(order);
        return "Order placed successfully";
    }

    // 🔥 THIS WAS MISSING
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
