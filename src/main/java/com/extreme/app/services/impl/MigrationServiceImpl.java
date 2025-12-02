package com.extreme.app.services.impl;

import com.extreme.app.entities.*;
import com.extreme.app.entities.enums.AttachmentType;
import com.extreme.app.entities.enums.StoragePath;
import com.extreme.app.repository.*;
import com.extreme.app.services.MigrationService;
import com.extreme.app.services.StorageService;
import com.extreme.app.utils.FileUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.ArrayList;

@Slf4j
@AllArgsConstructor
@ApplicationScoped
public class MigrationServiceImpl implements MigrationService {

    private final LegacyRepository legacyRepository;
    private final AssistedRepository assistedRepository;
    private final RepresentativeRepository representativeRepository;
    private final StorageService storageService;
    private final AssistedAttachmentRepository assistedAttachmentRepository;
    private final SolicitationLegacyRepository solicitationLegacyRepository;
    private final ProductSolicitationLegacyRepository productSolicitationLegacyRepository;
    private final ForwardingLegacyRepository forwardingLegacyRepository;
    private final OficioLegacyRepository oficioLegacyRepository;
    private final ReportLegacyRepository reportLegacyRepository;
    private final StatusDocumentSolicitationLegacyRepository statusDocumentSolicitationLegacyRepository;
    private final MedicalDocumentLegacyRepository medicalDocumentLegacyRepository;
    private final ProductToMedicalDocumentLegacyRepository productToMedicalDocumentLegacyRepository;
    private final CidToMedicalDocumentLegacyRepository cidToMedicalDocumentLegacyRepository;

    public void migratedAssisted() throws Exception {
        long start = System.currentTimeMillis();
        int sucess = 0;
        int erros = 0;

        var assistedList = legacyRepository.findAssistedByLegacy();
        var countAssisted = assistedRepository.count();

        int total = assistedList.size();

        if(!assistedList.isEmpty()) {
            for (AssistedEntity assisted : assistedList) {
                try {
                    this.saveMigratedAssisted(assisted);
                    sucess++;
                } catch (Exception e) {
                    erros++;
                    log.error("Erro ao migrar CPF " + assisted.getCpf() + ": " + e.getMessage());
                }
            }

            long end = System.currentTimeMillis();
            long durationMillis = end - start;
            double durationSeconds = durationMillis / 1000.0;

            log.info("=== RESULTADO DA MIGRAÇÃO ASSISTIDOS===");
            log.info("Total: " + total);
            log.info("Sucesso: " + sucess);
            log.info("Erros: " + erros);
            log.info("Duração: " + durationSeconds + " segundos");
            log.info("Migração de assistidos concluída.");
            log.info("Iniciando migração de representantes...");
            this.migratedRepresentative();
        }else {
            log.info("Nenhum assistido encontrado para migração.");
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveMigratedAssisted(AssistedEntity assisted){
        assistedRepository.getEntityManager().merge(assisted);
    }

    public void migratedRepresentative() throws Exception {
        long start = System.currentTimeMillis();
        int success = 0;
        int duplicated = 0;
        int errors = 0;

        var representativeList = legacyRepository.findRepresentativeByLegacy();
        var countRepresentatives = representativeRepository.count();
        int total = representativeList.size();

        if(!representativeList.isEmpty()){
            for (RepresentativeEntity representative : representativeList) {
                try {
                    this.processSingleRepresentative(representative);
                    success++;
                } catch (Exception e) {
                    errors++;
                    log.error("Erro ao migrar CPF {}: {}", representative.getCpf(), e.getMessage());

                    try {
                        representativeRepository.getEntityManager().clear();
                    } catch (Exception clearEx) {
                        log.warn("Erro ao limpar EntityManager: {}", clearEx.getMessage());
                    }
                }
            }

            long end = System.currentTimeMillis();
            double durationSeconds = (end - start) / 1000.0;

            log.info("=== RESULTADO DA MIGRAÇÃO REPRESENTANTES ===");
            log.info("Total: {}", total);
            log.info("Sucesso: {}", success);
            log.info("Duplicados: {}", duplicated);
            log.info("Erros: {}", errors);
            log.info("Duração: {} segundos", durationSeconds);
        }else {
            log.info("Nenhum representante encontrado para migração.");
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void processSingleRepresentative(RepresentativeEntity representative) {
        var existRepresentative = representativeRepository.findByCpf(representative.getCpf());

        if (existRepresentative.isPresent()) {
            var rep = existRepresentative.get();

            rep.getAssisteds().size();

            var assisted = assistedRepository.findAssistedByMigratedId(representative.getAssistedId());

            if (assisted.isPresent()) {
                var assistedEntity = assisted.get();

                if (!rep.getAssisteds().contains(assistedEntity)) {
                    rep.getAssisteds().add(assistedEntity);
                    representativeRepository.getEntityManager().merge(rep);
                    representativeRepository.getEntityManager().flush();

                    log.info("Associação adicionada: assisted [{}] -> representante [{}] (CPF: {})",
                            assistedEntity.getId(), rep.getMigratedId(), rep.getCpf());
                } else {
                    log.info("Assisted [{}] já associado ao representante [{}]",
                            assistedEntity.getId(), rep.getMigratedId());
                }
            }

            log.warn("CPF duplicado encontrado: {}", representative.getCpf());
            return;
        }

        var assisted = assistedRepository.findAssistedByMigratedId(representative.getAssistedId());
        if (assisted.isPresent()) {
            var assistedEntity = assisted.get();
            representative.getAssisteds().size();

            if (!representative.getAssisteds().contains(assistedEntity)) {
                representative.getAssisteds().add(assistedEntity);
            }
        }

        representativeRepository.getEntityManager().merge(representative);
        representativeRepository.getEntityManager().flush();

        log.info("Migrado novo representante [{}] CPF: {}",
                representative.getMigratedId(), representative.getCpf());
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    @Override
    public void migratedAssistedDocuments() throws Exception {
        long start = System.currentTimeMillis();
        var assistedList = assistedRepository.findAssistedWhereDocumentNotMigrated();

        int total = assistedList.size();
        int success = 0;
        int noDocument = 0;
        int errors = 0;

        if(!assistedList.isEmpty()){
            for (AssistedEntity assisted : assistedList) {
                try {
                    var document = legacyRepository.findAssistedDocumentByLegacy(assisted.getMigratedId());

                    if(document.isPresent()){
                        var doc = document.get();
                        var assistedAttachment = doc.toEntity(assisted);
                        var path = storageService.upload(doc.getData(),
                                FileUtils.detectContentTypeByExtension(doc.getFileName()),
                                StoragePath.ASSISTED);

                        assistedAttachment.setPath(path);
                        assistedAttachment.setSize((long) doc.getData().length);
                        assistedAttachment.setType(AttachmentType.fromLabel(FileUtils.detectContentTypeByExtension(doc.getFileName())));
                        this.saveAttachmentAndAssisted(assistedAttachment, assisted);
                        success++;
                    }else {
                        noDocument++;
                        this.saveAttachmentAndAssisted(null, assisted);
                        log.info("Assistido ID {} não possui documento para migrar.", assisted.getId());
                    }

                } catch (SQLException e) {
                    errors++;
                    log.error("Erro ao migrar documento do assistido ID {}: {}", assisted.getId(), e.getMessage());
                }
            }

            long end = System.currentTimeMillis();
            double durationSeconds = (end - start) / 1000.0;
            log.info("=== RESULTADO DA MIGRAÇÃO DOCUMENTOS ASSISTIDOS ===");
            log.info("Total: {}", total);
            log.info("Sucesso: {}", success);
            log.info("Sem documentos: {}", noDocument);
            log.info("Erros: {}", errors);
            log.info("Duração: {} segundos", durationSeconds);
        }else{
            log.info("Nenhum assistido encontrado para migração de documentos.");
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveAttachmentAndAssisted(AssistedAttachmentEntity attachment, AssistedEntity assisted) {
        assisted.setIsMigratedDocument(true);
        assistedRepository.getEntityManager().merge(assisted);
        if (attachment != null){
            assistedAttachmentRepository.persistAndFlush(attachment);
        }
    }

    public void migratedSolicitation() throws Exception {
        long start = System.currentTimeMillis();
        int sucess = 0;
        int erros = 0;
        var solicitationList = legacyRepository.findSolicitationByLegacy();

        int total = solicitationList.size();

        for (SolicitationLegacyEntity solicitation : solicitationList) {
            try {
                this.saveMigratedSolicitation(solicitation);
                log.info("Sucesso ao migrar Solicitação: " + solicitation.getMigratedId());
                sucess++;
            } catch (Exception e) {
                erros++;
                log.error("Erro ao migrar Solicitação: " + solicitation.getMigratedId() + ": " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        long durationMillis = end - start;
        double durationSeconds = durationMillis / 1000.0;

        log.info("=== RESULTADO DA MIGRAÇÃO DE SOLICITAÇÕES===");
        log.info("Total: " + total);
        log.info("Sucesso: " + sucess);
        log.info("Erros: " + erros);
        log.info("Duração: " + durationSeconds + " segundos");
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveMigratedSolicitation(SolicitationLegacyEntity solicitation){
        solicitationLegacyRepository.getEntityManager().merge(solicitation);
    }

    @Override
    public void migratedProductsSolicitation() throws Exception {
        long start = System.currentTimeMillis();
        int sucess = 0;
        int erros = 0;
        var productList = legacyRepository.findProductSolicitationByLegacy(productSolicitationLegacyRepository.findMaxMigratedId());
        var erroList = new ArrayList<String>();

        int total = productList.size();

        for (ProductSolicitationLegacyEntity product : productList) {
            try {
                this.saveMigratedProductSolicitation(product);
                sucess++;
            } catch (Exception e) {
                erros++;
                erroList.add("Erro ao migrar Item Solicitação: " + product.getMigratedId() + ": " + e.getMessage());
                log.error("Erro ao migrar Solicitação: " + product.getMigratedId() + ": " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        long durationMillis = end - start;
        double durationSeconds = durationMillis / 1000.0;

        log.info("=== RESULTADO DA MIGRAÇÃO PRODUTOS DA SOLICITAÇÃO ===");
        log.info("Total: " + total);
        log.info("Sucesso: " + sucess);
        log.info("Erros: " + erros);
        log.info("Lista de Erros:");
        for(String erro : erroList){
            log.info(erro);
        }
        log.info("Duração: " + durationSeconds + " segundos");
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveMigratedProductSolicitation(ProductSolicitationLegacyEntity product){
        productSolicitationLegacyRepository.getEntityManager().merge(product);
    }

    @Override
    public void migratedForwarding() throws Exception {
        long start = System.currentTimeMillis();
        int sucess = 0;
        int erros = 0;
        var forwardingList = legacyRepository.findForwardingByLegacy(forwardingLegacyRepository.findMaxProductId());
        var erroList = new ArrayList<String>();

        int total = forwardingList.size();

        for (ForwardingLegacyEntity forwarding : forwardingList) {
            try {
                this.saveMigratedForwarding(forwarding);
                sucess++;
            } catch (Exception e) {
                erros++;
                erroList.add("Erro ao migrar Item Solicitação: " + forwarding.getProductId() + ": " + e.getMessage());
                log.error("Erro ao migrar Solicitação: " + forwarding.getProductId() + ": " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        long durationMillis = end - start;
        double durationSeconds = durationMillis / 1000.0;

        log.info("=== RESULTADO DA MIGRAÇÃO DE ENCAMINHAMENTOS ===");
        log.info("Total: " + total);
        log.info("Sucesso: " + sucess);
        log.info("Erros: " + erros);
        log.info("Lista de Erros:");
        for(String erro : erroList){
            log.info(erro);
        }
        log.info("Duração: " + durationSeconds + " segundos");
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveMigratedForwarding(ForwardingLegacyEntity forwardingLegacy){
        forwardingLegacyRepository.getEntityManager().merge(forwardingLegacy);
    }

    @Override
    public void migratedOficio() throws Exception {
        long start = System.currentTimeMillis();
        int sucess = 0;
        int erros = 0;
        var oficioList = legacyRepository.findOficioByLegacy(oficioLegacyRepository.findMaxMigratedId());
        var erroList = new ArrayList<String>();

        int total = oficioList.size();

        for (OficioLegacyEntity oficio : oficioList) {
            try {
                this.saveMigratedOficio(oficio);
                sucess++;
            } catch (Exception e) {
                erros++;
                erroList.add("Erro ao migrar Oficio: " + oficio.getMigratedId() + ": " + e.getMessage());
                log.error("Erro ao migrar Oficio: " + oficio.getMigratedId() + ": " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        long durationMillis = end - start;
        double durationSeconds = durationMillis / 1000.0;

        log.info("=== RESULTADO DA MIGRAÇÃO DE OFICIOS ===");
        log.info("Total: " + total);
        log.info("Sucesso: " + sucess);
        log.info("Erros: " + erros);
        log.info("Lista de Erros:");
        for(String erro : erroList){
            log.info(erro);
        }
        log.info("Duração: " + durationSeconds + " segundos");
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveMigratedOficio(OficioLegacyEntity oficioLegacyEntity){
        oficioLegacyRepository.getEntityManager().merge(oficioLegacyEntity);
    }

    @Override
    public void migratedReport() throws Exception {
        long start = System.currentTimeMillis();
        int sucess = 0;
        int erros = 0;
        var reportList = legacyRepository.findReportByLegacy(reportLegacyRepository.findMaxMigratedId());
        var erroList = new ArrayList<String>();

        int total = reportList.size();

        for (ReportLegacyEntity report : reportList) {
            try {
                this.saveMigratedReport(report);
                sucess++;
            } catch (Exception e) {
                erros++;
                erroList.add("Erro ao migrar Parecer: " + report.getMigratedId() + ": " + e.getMessage());
                log.error("Erro ao migrar Parecer: " + report.getMigratedId() + ": " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        long durationMillis = end - start;
        double durationSeconds = durationMillis / 1000.0;

        log.info("=== RESULTADO DA MIGRAÇÃO DE PARECERES ===");
        log.info("Total: " + total);
        log.info("Sucesso: " + sucess);
        log.info("Erros: " + erros);
        log.info("Lista de Erros:");
        for(String erro : erroList){
            log.info(erro);
        }
        log.info("Duração: " + durationSeconds + " segundos");
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveMigratedReport(ReportLegacyEntity reportLegacyEntity){
        reportLegacyRepository.getEntityManager().merge(reportLegacyEntity);
    }

    @Override
    public void migratedStatusDocumentSolicitation() throws Exception {
        long start = System.currentTimeMillis();
        int sucess = 0;
        int erros = 0;
        var statusList = legacyRepository.findStatusDocumentSolicitationByLegacy(statusDocumentSolicitationLegacyRepository.findMaxMigratedId());
        var erroList = new ArrayList<String>();

        int total = statusList.size();

        for (StatusDocumentSolicitationLegacyEntity status : statusList) {
            try {
                this.saveMigratedStatusDocumentSolicitation(status);
                sucess++;
            } catch (Exception e) {
                erros++;
                erroList.add("Erro ao migrar Parecer: " + status.getMigratedId() + ": " + e.getMessage());
                log.error("Erro ao migrar Parecer: " + status.getMigratedId() + ": " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        long durationMillis = end - start;
        double durationSeconds = durationMillis / 1000.0;

        log.info("=== RESULTADO DA MIGRAÇÃO DE STATUS DOS DOCUMENTOS ===");
        log.info("Total: " + total);
        log.info("Sucesso: " + sucess);
        log.info("Erros: " + erros);
        log.info("Lista de Erros:");
        for(String erro : erroList){
            log.info(erro);
        }
        log.info("Duração: " + durationSeconds + " segundos");
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveMigratedStatusDocumentSolicitation(StatusDocumentSolicitationLegacyEntity statusDocumentSolicitationLegacyEntity){
        statusDocumentSolicitationLegacyRepository.getEntityManager().merge(statusDocumentSolicitationLegacyEntity);
    }

    @Override
    public void migratedMedicalDocument() throws Exception {
        long start = System.currentTimeMillis();
        int sucess = 0;
        int erros = 0;
        var medicalList = legacyRepository.findMedicalDocumentByLegacy(medicalDocumentLegacyRepository.findMaxMigratedId());
        var erroList = new ArrayList<String>();

        int total = medicalList.size();

        for (MedicalDocumentLegacyEntity medical : medicalList) {
            try {
                this.saveMigratedMedicalDocument(medical);
                sucess++;
            } catch (Exception e) {
                erros++;
                erroList.add("Erro ao migrar Documento médico: " + medical.getMigratedId() + ": " + e.getMessage());
                log.error("Erro ao migrar Documento médico: " + medical.getMigratedId() + ": " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        long durationMillis = end - start;
        double durationSeconds = durationMillis / 1000.0;

        log.info("=== RESULTADO DA MIGRAÇÃO DOS DOCUMENTOS MEDICOS ===");
        log.info("Total: " + total);
        log.info("Sucesso: " + sucess);
        log.info("Erros: " + erros);
        log.info("Lista de Erros:");
        for(String erro : erroList){
            log.info(erro);
        }
        log.info("Duração: " + durationSeconds + " segundos");
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveMigratedMedicalDocument(MedicalDocumentLegacyEntity medicalDocumentLegacyEntity){
        medicalDocumentLegacyRepository.getEntityManager().merge(medicalDocumentLegacyEntity);
    }

    @Override
    public void migratedProductToMedicalDocument() throws Exception {
        long start = System.currentTimeMillis();
        int sucess = 0;
        int erros = 0;
        var productToDocumentList = legacyRepository.findProductToMedicalDocumentByLegacy(productToMedicalDocumentLegacyRepository.findMaxMigratedId());
        var erroList = new ArrayList<String>();

        int total = productToDocumentList.size();

        for (ProductToMedicalDocumentLegacyEntity productToDocument : productToDocumentList) {
            try {
                this.saveMigratedProductToMedicalDocument(productToDocument);
                sucess++;
            } catch (Exception e) {
                erros++;
                erroList.add("Erro ao migrar Produtos dos Documento Médico: " + productToDocument.getMigratedId() + ": " + e.getMessage());
                log.error("Erro ao migrar Produtos dos Documento Médico: " + productToDocument.getMigratedId() + ": " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        long durationMillis = end - start;
        double durationSeconds = durationMillis / 1000.0;

        log.info("=== RESULTADO DA MIGRAÇÃO DOS PRODUTOS DOS DOCUMENTOS MEDICOS ===");
        log.info("Total: " + total);
        log.info("Sucesso: " + sucess);
        log.info("Erros: " + erros);
        log.info("Lista de Erros:");
        for(String erro : erroList){
            log.info(erro);
        }
        log.info("Duração: " + durationSeconds + " segundos");
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveMigratedProductToMedicalDocument(ProductToMedicalDocumentLegacyEntity productToMedicalDocumentLegacyEntity){
        productToMedicalDocumentLegacyRepository.getEntityManager().merge(productToMedicalDocumentLegacyEntity);
    }

    @Override
    public void migratedCidToMedicalDocument() throws Exception {
        long start = System.currentTimeMillis();
        int sucess = 0;
        int erros = 0;
        var cidToDocumentList = legacyRepository.findCidToMedicalDocumentByLegacy(cidToMedicalDocumentLegacyRepository.findMaxMigratedId());
        var erroList = new ArrayList<String>();

        int total = cidToDocumentList.size();

        for (CidToMedicalDocumentLegacyEntity cidToDocument : cidToDocumentList) {
            try {
                this.saveMigratedCidToMedicalDocument(cidToDocument);
                sucess++;
            } catch (Exception e) {
                erros++;
                erroList.add("Erro ao migrar Cids dos Documento Médico: " + cidToDocument.getMigratedId() + ": " + e.getMessage());
                log.error("Erro ao migrar Cids dos Documento Médico: " + cidToDocument.getMigratedId() + ": " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        long durationMillis = end - start;
        double durationSeconds = durationMillis / 1000.0;

        log.info("=== RESULTADO DA MIGRAÇÃO DOS CIDS DOS DOCUMENTOS MEDICOS ===");
        log.info("Total: " + total);
        log.info("Sucesso: " + sucess);
        log.info("Erros: " + erros);
        log.info("Lista de Erros:");
        for(String erro : erroList){
            log.info(erro);
        }
        log.info("Duração: " + durationSeconds + " segundos");
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveMigratedCidToMedicalDocument(CidToMedicalDocumentLegacyEntity cidToMedicalDocumentLegacyEntity){
        cidToMedicalDocumentLegacyRepository.getEntityManager().merge(cidToMedicalDocumentLegacyEntity);
    }
}

