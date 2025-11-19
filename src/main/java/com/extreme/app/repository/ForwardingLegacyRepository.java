package com.extreme.app.repository;

import com.extreme.app.entities.ForwardingLegacyEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ForwardingLegacyRepository implements PanacheRepository<ForwardingLegacyEntity> {

    public Long findMaxProductId() {
        return find("select max(f.migratedId) from ForwardingLegacyEntity f")
                .project(Long.class)
                .firstResult();
    }
}
