package com.extreme.app.repository;

import com.extreme.app.entities.ProductSolicitationLegacyEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductSolicitationLegacyRepository implements PanacheRepository<ProductSolicitationLegacyEntity> {

    public Long findMaxMigratedId() {
        return find("select max(i.migratedId) from ProductSolicitationLegacyEntity i")
                .project(Long.class)
                .firstResult();
    }
}
