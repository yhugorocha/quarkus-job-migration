package com.extreme.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oficio_legacy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OficioLegacyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "migrated_id", unique = true)
    private Long migratedId;

    @Column(name = "solicitation_id")
    private Long solicitationId;

    @Column(name = "number")
    private String number;

    @Column(name = "year")
    private String year;
}
