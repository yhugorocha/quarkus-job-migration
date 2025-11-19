package com.extreme.app.repository;

import com.extreme.app.entities.ReportLegacyEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReportLegacyRepository implements PanacheRepository<ReportLegacyEntity> {

    public Long findMaxMigratedId() {
        return find("select max(r.migratedId) from ReportLegacyEntity r")
                .project(Long.class)
                .firstResult();
    }
}
