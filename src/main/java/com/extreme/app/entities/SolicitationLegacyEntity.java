package com.extreme.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitation_legacy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitationLegacyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "migrated_id", unique = true)
    private Long migratedId;

    @Column(name = "assisted_id")
    private Long assistedId;

    @Column(name = "name_solicitation")
    private String nameSolicitation;

    @Column(name = "cpf_solicitation")
    private String cpfSolicitation;

    @Column(name = "phone_solicitation")
    private String phoneSolicitation;

    @Column(name = "solicitation_number")
    private String solicitationNumber;

    @Column(name = "number_e_paj")
    private String numberEPaj;

    @Column(name = "abandon_reason")
    private String abandonReason;

    @Column(name = "concluded_reason")
    private String concludedReason;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "height")
    private Double height;

    @Column(name = "imc")
    private Double imc;

    @Column(name = "observation")
    private String observation;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "status")
    private String status;

    @Column(name = "priority")
    private String priority;

    @Column(name = "defender")
    private String defender;

    @Column(name = "analysis")
    private String analysis;
}

