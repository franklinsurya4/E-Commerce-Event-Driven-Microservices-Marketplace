package com.example.inventory_services.Controller;

import com.example.inventory_services.DTO.InventoryResponse;
import com.example.inventory_services.Services.InventoryServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j // This resolves the 'log' error
public class InventoryController {

    private final InventoryServices inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        log.info("Received inventory check request for: {}", skuCode);
        return inventoryService.isInStock(skuCode);
    }
}