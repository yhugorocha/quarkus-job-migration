package com.extreme.app.startup;

import com.extreme.app.services.InteriorService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@Startup
@ApplicationScoped
public class AssistedInteriorInitializer {

    private final InteriorService interiorService;

    @ConfigProperty(name = "app.assisted.archive.name", defaultValue = "base_assisted.json")
    String archiveName;

    @ConfigProperty(name = "app.assisted.initializer.enabled", defaultValue = "true")
    boolean enabled;

    public AssistedInteriorInitializer(InteriorService interiorService) {
        this.interiorService = interiorService;
    }

    @PostConstruct
    @Transactional
     void init() throws Exception {
        try {
            //interiorService.importAssistedInterior(archiveName);
        } catch (Exception e) {
            log.error("Erro ao importar Assistidos: {}", e.getMessage(), e);
            throw new Exception("Erro ao importar Assistidos: " + e.getMessage());
        }
    }
}