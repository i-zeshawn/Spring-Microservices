package com.behlole.orderservice.service;

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
        /**
         * Call Inventory Service if product is in stock
         */
        Boolean result = webClient.get()
                .uri("http://localhost:8082/api/inventory")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        if (Boolean.TRUE.equals(result))
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
