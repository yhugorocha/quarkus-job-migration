package com.extreme.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_solicitation_legacy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSolicitationLegacyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "migrated_id", unique = true)
    private Long migratedId;

    @Column(name = "solicitation_id")
    private Long solicitation;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "classification")
    private String classification;

    @Column(name = "quantity_forwarding")
    private Integer quantityForwarding;

    @Column(name = "posology")
    private String posology;

    @Column(name = "status")
    private String status;

    @Column(name = "technical_analysis")
    private String technicalAnalysis;

    @Column(name = "report_id")
    private Long reportId;
}
