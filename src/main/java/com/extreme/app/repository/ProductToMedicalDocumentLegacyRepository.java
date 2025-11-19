package com.extreme.app.repository;

import com.extreme.app.entities.ProductToMedicalDocumentLegacyEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductToMedicalDocumentLegacyRepository implements PanacheRepository<ProductToMedicalDocumentLegacyEntity> {

    public Long findMaxMigratedId() {
        return find("select max(p.migratedId) from ProductToMedicalDocumentLegacyEntity p")
                .project(Long.class)
                .firstResult();
    }
}
