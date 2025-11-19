package com.extreme.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_document_legacy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicalDocumentLegacyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "migrated_id")
    private Long migratedId;

    @Column(name = "solicitation_id")
    private Long solicitationId;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "unity")
    private String unity;

    @Column(name = "bond")
    private String bond;

    @Column(name = "responsible_professional")
    private String responsibleProfessional;

    @Column(name = "advice")
    private String advice;

    @Column(name = "observation")
    private String observation;

    @Column(name = "other")
    private String other;
}
