package com.extreme.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_legacy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportLegacyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "migrated_id")
    private Long migratedId;

    @Column(name = "description")
    private String description;

    @Column(name = "date_analysis")
    private LocalDateTime dateAnalysis;

    @Column(name = "name_analyst")
    private String nameAnalyst;

    @Column(name = "secretary")
    private String secretary;
}
