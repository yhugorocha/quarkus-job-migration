package com.extreme.app.resources;

import com.extreme.app.services.MigrationService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Path("/migrar")
public class MigrationResource {

    private final MigrationService migrationService;

    @GET
    @Path("/solicitacoes")
    public void migratedSolicitation() throws Exception {
        migrationService.migratedSolicitation();
    }

}
