package com.extreme.app.services;

public interface MigrationService {

    void migratedAssisted() throws Exception;
    void migratedRepresentative() throws Exception;
    void migratedAssistedDocuments() throws Exception;
    void migratedSolicitation() throws Exception;
    void migratedProductsSolicitation() throws Exception;
    void migratedForwarding() throws Exception;
    void migratedOficio() throws Exception;
    void migratedReport() throws Exception;
    void migratedStatusDocumentSolicitation() throws Exception;
    void migratedMedicalDocument() throws Exception;
    void migratedProductToMedicalDocument() throws Exception;
    void migratedCidToMedicalDocument() throws Exception;
}