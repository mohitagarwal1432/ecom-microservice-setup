package com.company.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Column(name = "product_name")
    private String productName;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Version
    private Long version;
}
