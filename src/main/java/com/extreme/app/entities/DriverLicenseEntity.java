package com.extreme.app.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "driver_license")
public class DriverLicenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "issuing_authority")
    private String issuingAuthority;

    @Column(name = "issuing_state")
    private String issuingState;

}
