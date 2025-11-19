package com.extreme.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "forwarding_legacy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForwardingLegacyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "migrated_id", unique = true)
    private Long migratedId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "solicitation_id")
    private Long solicitationId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "posology")
    private String posology;

    @Column(name = "period")
    private String period;

    @Column(name = "category")
    private String category;

    @Column(name = "irregular_stock")
    private Boolean irregularStock;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "forwarding")
    private String forwarding;

    @Column(name = "unity")
    private String unity;

    @Column(name = "status")
    private String status;

    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "oficio_id")
    private Long oficioId;
}
