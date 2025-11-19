package com.extreme.app.entities;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "representative")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class RepresentativeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "migrated_id", unique = true)
    private Long migratedId;

    @ManyToMany
    @JoinTable(
            name = "representative_assisted",
            joinColumns = @JoinColumn(name = "representative_id"),
            inverseJoinColumns = @JoinColumn(name = "assisted_id")
    )
    public Set<AssistedEntity> assisteds = new HashSet<>();

    @Column(name = "name")
    private String name;

    @Column(name = "cpf", length = 11)
    private String cpf;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "identity_card_id")
    private IdentityCardEntity identityCard;

    @Column(name = "social_name")
    private String socialName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "relationship_type")
    private String relationshipType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_id")
    private ContactEntity contact;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Transient
    private Long assistedId;
}
