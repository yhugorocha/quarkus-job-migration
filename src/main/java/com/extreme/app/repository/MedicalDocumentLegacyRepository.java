package com.extreme.app.repository;

import com.extreme.app.entities.MedicalDocumentLegacyEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MedicalDocumentLegacyRepository implements PanacheRepository<MedicalDocumentLegacyEntity> {

    public Long findMaxMigratedId() {
        return find("select max(m.migratedId) from MedicalDocumentLegacyEntity m")
                .project(Long.class)
                .firstResult();
    }
}
