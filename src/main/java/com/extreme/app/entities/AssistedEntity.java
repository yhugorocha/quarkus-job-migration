package com.extreme.app.entities;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "assisted")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class AssistedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "migrated_id", unique = true)
    private Long migratedId;

    @Column(name = "name")
    private String name;

    @Column(name = "social_name")
    private String socialName;

    @Column(name = "cpf", length = 11, unique = true)
    private String cpf;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "identity_card_id")
    private IdentityCardEntity identityCard;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_license_id")
    private DriverLicenseEntity driverLicense;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "person_with_disability")
    private Boolean personWithDisability;

    @Column(name = "sub_register")
    private Boolean subRegister;

    @Column(name = "transgender_person")
    private Boolean transgenderPerson;

    @Column(name = "sexual_orientation")
    private String sexualOrientation;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "birthplace")
    private String birthplace;

    @Column(name = "cns")
    private String cns;

    @Column(name = "deceased")
    private Boolean deceased;

    @Column(name = "date_of_death")
    private LocalDate dateOfDeath;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_id")
    private ContactEntity contact;

    @OneToMany(mappedBy = "assisted", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @ToString.Exclude
    private List<AssistedAttachmentEntity> attachment;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "is_migrated_document")
    private Boolean isMigratedDocument;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssistedEntity assisted = (AssistedEntity) o;
        return Objects.equals(id, assisted.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
