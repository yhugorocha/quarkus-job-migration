package com.extreme.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_to_medical_document_legacy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductToMedicalDocumentLegacyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "migrated_id")
    private Long migratedId;

    @Column(name = "medical_document_id")
    private Long medicalDocumentId;

    @Column(name = "posology")
    private String posology;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "period")
    private String period;
}

