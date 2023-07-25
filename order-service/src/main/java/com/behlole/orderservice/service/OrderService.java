package com.behlole.orderservice.service;

import com.behlole.orderservice.dto.InventoryDto;
import com.behlole.orderservice.dto.OrderLineItemsDto;
import com.behlole.orderservice.dto.OrderRequest;
import com.behlole.orderservice.model.Order;
import com.behlole.orderservice.model.OrderLineItems;
import com.behlole.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream().map(this::mapToDTO).toList();
        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order
                .getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode).toList();
        /**
         * Call Inventory Service if product is in stock
         */
        InventoryDto[] inventoryResponseArray = webClient.get()
                .uri("http://127.0.0.1:8082/api/inventory", uriBuilder ->
                        uriBuilder.queryParam("skuCode", skuCodes).build()
                )
                .retrieve()
                .bodyToMono(InventoryDto[].class)
                .block();
        assert inventoryResponseArray != null;
        boolean allProductsIsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryDto::getIsInStock);
        if (allProductsIsInStock)
            orderRepository.save(order);
        else
            throw new IllegalArgumentException("Product is not in stock");
    }

    private OrderLineItems mapToDTO(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
