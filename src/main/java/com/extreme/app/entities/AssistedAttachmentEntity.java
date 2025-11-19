package com.extreme.app.entities;

import com.extreme.app.entities.enums.AttachmentType;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "assisted_attachment")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class AssistedAttachmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "migrated_id", unique = true)
    private Long migratedId;

    @ManyToOne
    @JoinColumn(name = "assisted_id")
    @ToString.Exclude
    private AssistedEntity assisted;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AttachmentType type;

    @Column(name = "name")
    private String name;

    @Column(name = "size")
    private Long size;

    @Column(name = "path")
    private String path;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

}
