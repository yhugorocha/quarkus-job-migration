package com.extreme.app.repository;

import com.extreme.app.entities.AssistedAttachmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AssistedAttachmentRepository implements PanacheRepository<AssistedAttachmentEntity> {
}
