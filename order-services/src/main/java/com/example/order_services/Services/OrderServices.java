package com.example.order_services.Services;

import com.example.order_services.DTO.*;
import com.example.order_services.Model.*;
import com.example.order_services.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServices {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        // Call Inventory Service using Service Discovery name
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        if (inventoryResponseArray == null) {
            throw new IllegalStateException("Inventory service returned no data");
        }

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);

        // Crucial check: Ensure we got a response for EVERY SKU we asked for
        if (allProductsInStock && inventoryResponseArray.length == skuCodes.size()) {
            orderRepository.save(order);
            log.info("Order {} saved successfully", order.getOrderNumber());
            return "Order Placed Successfully";
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto dto) {
        OrderLineItems items = new OrderLineItems();
        items.setPrice(dto.getPrice());
        items.setQuantity(dto.getQuantity());
        items.setSkuCode(dto.getSkuCode());
        return items;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}