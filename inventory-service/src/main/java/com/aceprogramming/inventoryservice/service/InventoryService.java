package com.aceprogramming.inventoryservice.service;

import com.aceprogramming.inventoryservice.dto.InventoryResponse;
import com.aceprogramming.inventoryservice.respository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

//    @Transactional(readOnly = true)
//    public boolean isInStock(String skuCode){
//        return inventoryRepository.findBySkuCode(skuCode).isPresent();
//    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode){
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                    InventoryResponse.builder()
                            .skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity()>0)
                            .build()
                ).toList();
    }
}
