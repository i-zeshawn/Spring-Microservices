package com.behlole.inventoryservice.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class InventoryDto {
    private String skuCode;
    private Boolean isInStock;
}
