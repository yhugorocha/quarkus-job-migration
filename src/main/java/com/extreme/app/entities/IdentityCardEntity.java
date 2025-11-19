package com.extreme.app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "identity_card")
public class IdentityCardEntity {

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

    @Column(name = "issue_date")
    private LocalDate issueDate;
}
