package com.extreme.app.entities;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contact")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class ContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "home_phone", length = 20)
    private String homePhone;

    @Column(name = "mobile_phone", length = 20)
    private String mobilePhone;

    @Column(name = "message_phone", length = 20)
    private String messagePhone;

    @Column(name = "email")
    private String email;
}
