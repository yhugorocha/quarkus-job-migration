package com.extreme.app.repository;

import com.extreme.app.entities.StatusDocumentSolicitationLegacyEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StatusDocumentSolicitationLegacyRepository implements PanacheRepository<StatusDocumentSolicitationLegacyEntity> {

    public Long findMaxMigratedId() {
        return find("select max(s.migratedId) from StatusDocumentSolicitationLegacyEntity s")
                .project(Long.class)
                .firstResult();
    }
}
