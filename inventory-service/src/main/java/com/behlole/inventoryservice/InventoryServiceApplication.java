package com.behlole.inventoryservice;

import com.behlole.inventoryservice.model.Inventory;
import com.behlole.inventoryservice.repositories.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            Inventory inventory = new Inventory();
            inventory.setSkuCode("iphone_13");
            inventory.setQuantity(13);
            inventoryRepository.save(inventory);
            Inventory inventory2 = new Inventory();
            inventory2.setSkuCode("iphone_13_red");
            inventory2.setQuantity(0);
            inventoryRepository.save(inventory2);

        };
    }
}
