package com.extreme.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "status_document_solicitation_legacy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusDocumentSolicitationLegacyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "migrated_id")
    private Long migratedId;

    @Column(name = "received")
    private Boolean received;

    @Column(name = "solicitation_id")
    private Long solicitationId;

    @Column(name = "document_type")
    private String documentType;
}

