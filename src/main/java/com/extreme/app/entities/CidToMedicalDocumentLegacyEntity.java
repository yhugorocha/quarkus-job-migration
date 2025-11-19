package com.extreme.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cid_to_medical_document_legacy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CidToMedicalDocumentLegacyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "migrated_id")
    private Long migratedId;

    @Column(name = "medical_document_id")
    private Long medicalDocumentId;

    @Column(name = "cid_description")
    private String cidDescription;

    @Column(name = "diagnosis_date")
    private LocalDateTime diagnosisDate;

    @Column(name = "medical_diagnosis")
    private String medicalDiagnosis;
}

