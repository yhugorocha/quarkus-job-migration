package com.extreme.app.repository;

import com.extreme.app.entities.SolicitationLegacyEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SolicitationLegacyRepository implements PanacheRepository<SolicitationLegacyEntity> {
}
