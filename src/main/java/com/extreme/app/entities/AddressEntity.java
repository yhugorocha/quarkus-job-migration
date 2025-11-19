package com.extreme.app.entities;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "is_homeless")
    private Boolean isHomeless;

    @Column(name = "postal_code", length = 8)
    private String postalCode;

    @Column(name = "street", length = 150)
    private String street;

    @Column(name = "number")
    private String number;

    @Column(name = "complement", length = 150)
    private String complement;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "fu", length = 2)
    private String fu;

    @Column(name = "neighborhood", length = 150)
    private String neighborhood;

    @Column(name = "ap")
    private String ap;

    @Column(name = "unit")
    private String unit;
}
