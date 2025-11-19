package com.extreme.app.repository;

import com.extreme.app.entities.RepresentativeEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class RepresentativeRepository implements PanacheRepository<RepresentativeEntity> {

    public Optional<RepresentativeEntity> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }
}
