package com.extreme.app.repository;

import com.extreme.app.entities.SolicitationLegacyEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class SolicitationLegacyRepository implements PanacheRepository<SolicitationLegacyEntity> {

    List<SolicitationLegacyEntity> findByAssistedId(Long assistedId) {
        return list("assistedId", assistedId);
    }
}
