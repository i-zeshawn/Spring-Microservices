package com.behlole.inventoryservice.service;

import com.behlole.inventoryservice.dtos.InventoryDto;
import com.behlole.inventoryservice.model.Inventory;
import com.behlole.inventoryservice.repositories.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryDto> isInStock(List<String> skuCode) {
        List<InventoryDto> inventoryDtos= inventoryRepository.findBySkuCodeIn(skuCode).stream().map(inventory ->
                InventoryDto
                        .builder()
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() > 0)
                        .build()
        ).toList();
        System.out.println(inventoryDtos);
        return inventoryDtos;
    }
}
