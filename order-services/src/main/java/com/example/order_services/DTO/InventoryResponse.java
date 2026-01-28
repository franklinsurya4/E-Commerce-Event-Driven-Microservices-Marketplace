package com.example.order_services.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InventoryResponse {
    private String skuCode;

    @JsonProperty("inStock")
    private boolean inStock;
}
