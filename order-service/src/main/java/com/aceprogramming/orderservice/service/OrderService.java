package com.aceprogramming.orderservice.service;

import com.aceprogramming.orderservice.dto.OrderLineItemsDto;
import com.aceprogramming.orderservice.dto.OrderRequest;
import com.aceprogramming.orderservice.model.Order;
import com.aceprogramming.orderservice.model.OrderLineItems;
import com.aceprogramming.orderservice.respository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(orderLineItemsDto -> maptoDto(orderLineItemsDto))
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        //Call Inventory Service, and place order if product is in stock

        Boolean result = webClient.get()
                .uri("http://localhost:8082/api/inventory")
                .retrieve()
                .bodyToMono(boolean.class)
                .block(); // block is used to make synchronous


        orderRepository.save(order);

    }

    private OrderLineItems maptoDto(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
