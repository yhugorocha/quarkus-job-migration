package com.extreme.app.repository;

import com.extreme.app.entities.OficioLegacyEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OficioLegacyRepository implements PanacheRepository<OficioLegacyEntity> {

    public Long findMaxMigratedId() {
        return find("select max(o.migratedId) from OficioLegacyEntity o")
                .project(Long.class)
                .firstResult();
    }
}
