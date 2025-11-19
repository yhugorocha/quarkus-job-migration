package com.extreme.app.repository;

import com.extreme.app.entities.CidToMedicalDocumentLegacyEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CidToMedicalDocumentLegacyRepository implements PanacheRepository<CidToMedicalDocumentLegacyEntity> {

    public Long findMaxMigratedId() {
        return find("select max(c.migratedId) from CidToMedicalDocumentLegacyEntity c")
                .project(Long.class)
                .firstResult();
    }
}
