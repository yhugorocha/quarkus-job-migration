package com.extreme.app.config;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DatabaseConfig {

    @Inject
    AgroalDataSource defaultDataSource;

    @Inject
    @DataSource("legacy")
    AgroalDataSource legacyDataSource;

    public AgroalDataSource getMain() {
        return defaultDataSource;
    }

    public AgroalDataSource getLegacy() {
        return legacyDataSource;
    }
}
