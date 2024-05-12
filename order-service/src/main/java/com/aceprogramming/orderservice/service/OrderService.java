package com.aceprogramming.orderservice.service;

import com.aceprogramming.orderservice.dto.InventoryResponse;
import com.aceprogramming.orderservice.dto.OrderLineItemsDto;
import com.aceprogramming.orderservice.dto.OrderRequest;
import com.aceprogramming.orderservice.model.Order;
import com.aceprogramming.orderservice.model.OrderLineItems;
import com.aceprogramming.orderservice.respository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final WebClient.Builder  webClientBuilder;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(orderLineItemsDto -> maptoDto(orderLineItemsDto))
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        //Call Inventory Service, and place order if product is in stock

        InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block(); // block is used to make synchronous

        boolean allProductsInStock = Arrays.stream(inventoryResponsesArray).
                allMatch(InventoryResponse::isInStock);

        if(allProductsInStock){
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Product is not in stock, please try agian later");
        }



    }

    private OrderLineItems maptoDto(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
