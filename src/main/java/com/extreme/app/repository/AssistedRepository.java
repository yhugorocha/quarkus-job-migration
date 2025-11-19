package com.extreme.app.repository;

import com.extreme.app.entities.AssistedEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AssistedRepository implements PanacheRepository<AssistedEntity> {

    public Optional<AssistedEntity> findAssistedByMigratedId(Long migratedId){
        return find("migratedId", migratedId).firstResultOptional();
    }

    public List<AssistedEntity> findAssistedWhereDocumentNotMigrated() {
        return find("isMigratedDocument = false OR isMigratedDocument IS NULL")
                .range(0, 1000)
                .list();
    }

    public Long findMaxMigratedId() {
        return find("select max(a.migratedId) from AssistedEntity a")
                .project(Long.class)
                .firstResult();
    }
}
