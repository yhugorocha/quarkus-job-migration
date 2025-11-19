package schedulers;

import com.extreme.app.services.MigrationService;
import io.quarkus.scheduler.Scheduled;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class MigrationExecutionScheduled {

    private final MigrationService migrationService;

    @Scheduled(every = "30m")
    public void migratedAssisted() throws Exception {
        log.info("Iniciando a migração de assistidos...");
        migrationService.migratedAssisted();
    }

    @Scheduled(every = "60m")
    public void migratedAssistedDocument() throws Exception {
        //log.info("Iniciando a migração de documentos de assistidos...");
        //migrationService.migratedAssistedDocuments();
    }

    @Scheduled(every = "20m")
    public void migratedItemSolicitation() throws Exception {
        //log.info("Iniciando a migração de itens da solicitação...");
        //migrationService.migratedItemsSolicitation();
    }

    @Scheduled(every = "20m")
    public void migratedForwarding() throws Exception {
        //log.info("Iniciando a migração de Encaminhamentos...");
        //migrationService.migratedForwarding();
    }

    @Scheduled(every = "20m")
    public void migratedOficio() throws Exception {
        //log.info("Iniciando a migração de Oficios...");
        //migrationService.migratedOficio();
    }

    @Scheduled(every = "30m")
    public void migratedReport() throws Exception {
        //log.info("Iniciando a migração dos pareceres...");
        //migrationService.migratedReport();
    }

    @Scheduled(every = "30m")
    public void migratedStatusDocumentSolicitation() throws Exception {
        log.info("Iniciando a migração dos status dos documentos da solicitação...");
        migrationService.migratedStatusDocumentSolicitation();
    }

    @Scheduled(every = "30m")
    public void migratedMedicalDocument() throws Exception {
        //log.info("Iniciando a migração dos documentos Medicos...");
        //migrationService.migratedMedicalDocument();
    }

    @Scheduled(every = "30m")
    public void migratedProductToMedicalDocument() throws Exception {
        //log.info("Iniciando a migração dos produtos ligados aos documentos medicos...");
        //migrationService.migratedProductToMedicalDocument();
    }

    @Scheduled(every = "30m")
    public void migratedCidToMedicalDocument() throws Exception {
        //log.info("Iniciando a migração dos cids ligados aos documentos medicos...");
        //migrationService.migratedCidToMedicalDocument();
    }

}
