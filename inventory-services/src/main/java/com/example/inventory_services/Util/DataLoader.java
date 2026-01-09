package com.example.inventory_services.Util;

import com.example.inventory_services.Model.Inventory;
import com.example.inventory_services.Repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {

        // Insert only if SKU doesn't exist to prevent duplicates
        insertIfNotExists("iphone_13", 100);
        insertIfNotExists("oneplus_12", 200);
        insertIfNotExists("macbook_m1", 50);  // optional extra SKU
    }

    private void insertIfNotExists(String skuCode, int quantity) {
        Optional<Inventory> existing = inventoryRepository.findBySkuCodeIn(
                java.util.List.of(skuCode)
        ).stream().findFirst();

        if (existing.isEmpty()) {
            Inventory inventory = new Inventory();
            inventory.setSkuCode(skuCode);
            inventory.setQuantity(quantity);
            inventoryRepository.save(inventory);
        }
    }
}

