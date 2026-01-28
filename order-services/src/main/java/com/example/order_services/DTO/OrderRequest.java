package com.example.order_services.DTO;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
