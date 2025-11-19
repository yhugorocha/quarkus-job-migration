package com.extreme.app.repository;

import com.extreme.app.config.DatabaseConfig;
import com.extreme.app.dto.FileLegacy;
import com.extreme.app.entities.*;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@ApplicationScoped
public class LegacyRepository {

    @Inject
    DatabaseConfig databaseConfig;

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<AssistedEntity> findAssistedByLegacy() throws SQLException {

        var assisteds = new ArrayList<AssistedEntity>();

        StringBuilder sb = new StringBuilder();
        sb.append("WITH base AS (");
        sb.append("    SELECT ");
        sb.append("        p.id, ");
        sb.append("        p.nome, ");
        sb.append("        p.NomeSocial, ");
        sb.append("        REPLACE(REPLACE(REPLACE(REPLACE(p.cpf, '.', ''), '-', ''), '/', ''), ' ', '') AS cpf, ");
        sb.append("        REPLACE(REPLACE(REPLACE(REPLACE(p.Rg, '.', ''), '-', ''), '/', ''), ' ', '') AS Rg, ");
        sb.append("        s.Pai, ");
        sb.append("        s.Mae, ");
        sb.append("        s.DataNascimento, ");
        sb.append("        n.Descricao AS Nacionalidade, ");
        sb.append("        n2.Descricao AS Naturalidade, ");
        sb.append("        REPLACE(REPLACE(REPLACE(s.Cns, '.', ''), '-', ''), '/', '') AS Cns, ");
        sb.append("        REPLACE(REPLACE(p.Cep, '-', ''), '.', '') AS Cep, ");
        sb.append("        p.Logradouro, ");
        sb.append("        p.Numero, ");
        sb.append("        p.Complemento, ");
        sb.append("        u.Descricao AS Cidade, ");
        sb.append("        u.Sigla, ");
        sb.append("        p.Bairro, ");
        sb.append("        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(p.Telefone, '(', ''), ')', ''), '-', ''), ' ', ''), '.', '') AS Telefone, ");
        sb.append("        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(p.Celular, '(', ''), ')', ''), '-', ''), ' ', ''), '.', '') AS Celular, ");
        sb.append("        s.EmailSolicitante, ");
        sb.append("        ROW_NUMBER() OVER (PARTITION BY  ");
        sb.append("            REPLACE(REPLACE(REPLACE(REPLACE(p.cpf, '.', ''), '-', ''), '/', ''), ' ', '') ");
        sb.append("            ORDER BY p.id DESC ");
        sb.append("        ) AS rn ");
        sb.append("    FROM Pessoa p ");
        sb.append("    LEFT JOIN Solicitante s ON s.Pessoa_id = p.Id ");
        sb.append("    LEFT JOIN Nacionalidade n ON n.Id = p.Nacionalidade_id ");
        sb.append("    LEFT JOIN Naturalidade n2 ON n2.Id = p.Naturalidade_id ");
        sb.append("    LEFT JOIN UF u ON u.Id = p.Uf_id ");
        sb.append("    WHERE  ");
        sb.append("        p.cpf IS NOT NULL  ");
        sb.append("        AND p.cpf != ''  ");
        sb.append("        AND LEN(REPLACE(REPLACE(REPLACE(REPLACE(p.cpf, '.', ''), '-', ''), '/', ''), ' ', '')) = 11 ");
        sb.append("        AND EXISTS ( ");
        sb.append("            SELECT 1  ");
        sb.append("            FROM Solicitacao s2  ");
        sb.append("            WHERE s2.Solicitante_id = p.Id ");
        sb.append("        ) ");
        sb.append(") ");
        sb.append("SELECT * ");
        sb.append("FROM base ");
        sb.append("WHERE rn = 1;");

        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    var assisted = new AssistedEntity();
                    var address = new AddressEntity();
                    var contact = new ContactEntity();
                    var identityCard = new IdentityCardEntity();

                    assisted.setMigratedId(rs.getLong("id"));
                    assisted.setName(rs.getString("nome"));
                    assisted.setSocialName(rs.getString("NomeSocial"));
                    assisted.setCpf(rs.getString("cpf"));
                    assisted.setFatherName(rs.getString("Pai"));
                    assisted.setMotherName(rs.getString("Mae"));
                    assisted.setCns(rs.getString("Cns"));
                    assisted.setNationality(rs.getString("Nacionalidade"));
                    assisted.setBirthplace(rs.getString("Naturalidade"));

                    var dateOfBirth = rs.getDate("DataNascimento");
                    if (dateOfBirth != null) {
                        assisted.setDateOfBirth(dateOfBirth.toLocalDate());
                    }

                    address.setPostalCode(rs.getString("Cep"));
                    address.setStreet(rs.getString("Logradouro"));
                    address.setNumber(rs.getString("Numero"));
                    address.setComplement(rs.getString("Complemento"));
                    address.setCity(rs.getString("Cidade"));
                    address.setFu(rs.getString("Sigla"));
                    address.setNeighborhood(rs.getString("Bairro"));

                    contact.setHomePhone(rs.getString("Telefone"));
                    contact.setMobilePhone(rs.getString("Celular"));
                    contact.setEmail(rs.getString("EmailSolicitante"));

                    identityCard.setNumber(rs.getString("Rg"));

                    assisted.setAddress(address);
                    assisted.setContact(contact);
                    assisted.setIdentityCard(identityCard);
                    assisted.setDateCreated(LocalDateTime.now());
                    assisted.setIsDeleted(false);

                    assisteds.add(assisted);
                }
            }
        }

        return assisteds;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<RepresentativeEntity> findRepresentativeByLegacy() throws SQLException {
        var representativeList = new ArrayList<RepresentativeEntity>();

        StringBuilder sql = new StringBuilder();

        sql.append("WITH base AS ( ");
        sql.append("    SELECT ");
        sql.append("        r.Pessoa_id AS id, ");
        sql.append("        r.Solicitante_id AS solicitante_id, ");
        sql.append("        p.Nome, ");
        sql.append("        p.NomeSocial, ");
        sql.append("        REPLACE(REPLACE(REPLACE(REPLACE(p.cpf, '.', ''), '-', ''), '/', ''), ' ', '') AS cpf, ");
        sql.append("        REPLACE(REPLACE(p.Cep, '-', ''), '.', '') AS Cep, ");
        sql.append("        p.Logradouro, ");
        sql.append("        p.Numero, ");
        sql.append("        p.Complemento, ");
        sql.append("        u.Descricao AS Cidade, ");
        sql.append("        u.Sigla, ");
        sql.append("        p.Bairro, ");
        sql.append("        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(p.Telefone, '(', ''), ')', ''), '-', ''), ' ', ''), '.', '') AS Telefone, ");
        sql.append("        REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(p.Celular, '(', ''), ')', ''), '-', ''), ' ', ''), '.', '') AS Celular, ");
        sql.append("        ROW_NUMBER() OVER ( ");
        sql.append("            PARTITION BY REPLACE(REPLACE(REPLACE(REPLACE(p.cpf, '.', ''), '-', ''), '/', ''), ' ', '') ");
        sql.append("            ORDER BY r.Pessoa_id DESC ");
        sql.append("        ) AS rn ");
        sql.append("    FROM Representante r ");
        sql.append("    LEFT JOIN Pessoa p ON r.Pessoa_id = p.Id ");
        sql.append("    LEFT JOIN UF u ON u.Id = p.Uf_id ");
        sql.append("    WHERE ");
        sql.append("        p.Cpf IS NOT NULL ");
        sql.append("        AND p.cpf != '' ");
        sql.append("        AND LEN(REPLACE(REPLACE(REPLACE(REPLACE(p.cpf, '.', ''), '-', ''), '/', ''), ' ', '')) = 11 ");
        sql.append(") ");
        sql.append("SELECT * ");
        sql.append("FROM base ");
        sql.append("WHERE rn = 1; ");

        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var representative = new RepresentativeEntity();
                    var address = new AddressEntity();
                    var contact = new ContactEntity();

                    representative.setMigratedId(rs.getLong("id"));
                    representative.setAssistedId(rs.getLong("solicitante_id"));
                    representative.setName(rs.getString("Nome"));
                    representative.setSocialName(rs.getString("NomeSocial"));
                    representative.setCpf(rs.getString("cpf"));
                    address.setPostalCode(rs.getString("Cep"));
                    address.setStreet(rs.getString("Logradouro"));
                    address.setNumber(rs.getString("Numero"));
                    address.setComplement(rs.getString("Complemento"));
                    address.setCity(rs.getString("Cidade"));
                    address.setFu(rs.getString("Sigla"));
                    address.setNeighborhood(rs.getString("Bairro"));

                    contact.setHomePhone(rs.getString("Telefone"));
                    contact.setMobilePhone(rs.getString("Celular"));
                    representative.setAddress(address);
                    representative.setContact(contact);
                    representative.setDateCreated(LocalDateTime.now());
                    representative.setIsDeleted(false);
                    representativeList.add(representative);

                }
            }
        }

        return representativeList;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public Optional<FileLegacy> findAssistedDocumentByLegacy(Long assistedMigratedId) throws SQLException {
        FileLegacy fileLegacy = null;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("a.Id AS id, ");
        sql.append("a.Solicitante_id AS assisted_id, ");
        sql.append("a.Solicitacao_id AS solicitation_id, ");
        sql.append("a.Nome AS name, ");
        sql.append("a.Arquivo AS data ");
        sql.append("FROM Anexo a ");
        sql.append("WHERE a.Solicitante_id = ?");

        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setLong(1, assistedMigratedId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    fileLegacy = new FileLegacy();
                    fileLegacy.setId(rs.getLong("id"));
                    fileLegacy.setAssistedId(rs.getLong("assisted_id"));
                    fileLegacy.setSolicitationId(rs.getLong("solicitation_id"));
                    fileLegacy.setFileName(rs.getString("name"));
                    fileLegacy.setData(rs.getBytes("data"));
                }
            }
        }

        return Optional.ofNullable(fileLegacy);
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<SolicitationLegacyEntity> findSolicitationByLegacy() throws SQLException {
        var solicitationList = new ArrayList<SolicitationLegacyEntity>();
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append("s.id AS id, ");
        query.append("s.Solicitante_id as assisted_id, ");
        query.append("s.Nome AS name_solicitation, ");
        query.append("s.Cpf AS cpf_solicitation, ");
        query.append("s.Telefone AS phone_solicitation, ");
        query.append("s.CodigoSolicitacao AS solicitation_number, ");
        query.append("s.NumeroPaj AS number_e_paj, ");
        query.append("s.JustificativaAbandono AS abandon_reason, ");
        query.append("s.JustificativaConclusaoTriagem AS concluded_reason, ");
        query.append("s.Peso as weight, ");
        query.append("s.Altura as height, ");
        query.append("s.Imc as imc, ");
        query.append("s.ObservacaoDaAnalise AS observation, ");
        query.append("s.DataControle AS date_created, ");
        query.append("dv_status.Descricao AS status, ");
        query.append("dv_priority.Descricao AS priority, ");
        query.append("dv_defender.Descricao AS defender, ");
        query.append("dv_analysis.Descricao AS analysis ");
        query.append("FROM Solicitacao s ");
        query.append("LEFT JOIN Solicitante s2 ON s2.Pessoa_id = s.Id ");
        query.append("LEFT JOIN Pessoa p ON p.Id = s2.Pessoa_id ");
        query.append("LEFT JOIN DominioValor dv_status ON dv_status.Id = s.Status_id ");
        query.append("LEFT JOIN DominioValor dv_priority ON dv_priority.Id = s.Prioridade_id ");
        query.append("LEFT JOIN DominioValor dv_defender ON dv_defender.Id = s.Defensoria_id ");
        query.append("LEFT JOIN DominioValor dv_analysis ON dv_analysis.Id = s.SecretariaControle_id ");


        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var solicitation = new SolicitationLegacyEntity();
                    solicitation.setMigratedId(rs.getLong("id"));
                    solicitation.setAssistedId(rs.getLong("assisted_id"));
                    solicitation.setNameSolicitation(rs.getString("name_solicitation"));
                    solicitation.setCpfSolicitation(rs.getString("cpf_solicitation"));
                    solicitation.setPhoneSolicitation(rs.getString("phone_solicitation"));
                    solicitation.setSolicitationNumber(rs.getString("solicitation_number"));
                    solicitation.setNumberEPaj(rs.getString("number_e_paj"));
                    solicitation.setAbandonReason(rs.getString("abandon_reason"));
                    solicitation.setConcludedReason(rs.getString("concluded_reason"));
                    solicitation.setWeight(rs.getDouble("weight"));
                    solicitation.setHeight(rs.getDouble("height"));
                    solicitation.setImc(rs.getDouble("imc"));
                    solicitation.setObservation(rs.getString("observation"));
                    solicitation.setDateCreated(rs.getTimestamp("date_created").toLocalDateTime());
                    solicitation.setStatus(rs.getString("status"));
                    solicitation.setPriority(rs.getString("priority"));
                    solicitation.setDefender(rs.getString("defender"));
                    solicitation.setAnalysis(rs.getString("analysis"));

                    solicitationList.add(solicitation);
                }
            }
        }

        return solicitationList;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<ProductSolicitationLegacyEntity> findProductSolicitationByLegacy(Long id) throws SQLException {
        long baseId = (id == null ? 0 : id);
        long maxId = baseId + 50_000;
        var itemList = new ArrayList<ProductSolicitationLegacyEntity>();

        var query = """
                    SELECT
                    is2.id as migrated_id,
                    is2.Solicitacao_id as solicitation_id,
                    is2.Quantidade as quantity,
                    p.Codigo as product_code,
                    p.Nome as product_name,
                    fo.Nome as classification,
                    e.Quantidade as quantity_forwarding,
                    e.Posologia as posology,
                    dv_status.Descricao AS status,
                    dv_analysis.Descricao AS technical_analysis,
                    is2.ParecerControle_id as report_id
                    FROM ItemSolicitacao is2
                    LEFT JOIN Produto p  ON p.Id  = is2.Produto_id
                    LEFT JOIN FormaOrganizacional fo  ON p.FormaOrganizacional_id = fo.id
                    LEFT JOIN Encaminhamento e  ON e.Id  = is2.EncaminhamentoControle_id
                    LEFT JOIN DominioValor dv_status ON dv_status.Id = is2.Status_id
                    LEFT JOIN DominioValor dv_analysis ON dv_analysis.Id = is2.Secretaria_id
                    WHERE is2.Solicitacao_id IS NOT NULL AND is2.Id > ? AND is2.Id <= ?
                """;

        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, baseId);
            ps.setLong(2, maxId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var productSolicitation = new ProductSolicitationLegacyEntity();

                    productSolicitation.setMigratedId(rs.getLong("migrated_id"));
                    productSolicitation.setSolicitation(rs.getLong("solicitation_id"));
                    productSolicitation.setQuantity(rs.getInt("quantity"));
                    productSolicitation.setProductCode(rs.getString("product_code"));
                    productSolicitation.setProductName(rs.getString("product_name"));
                    productSolicitation.setClassification(rs.getString("classification"));
                    productSolicitation.setQuantityForwarding(rs.getInt("quantity_forwarding"));
                    productSolicitation.setPosology(rs.getString("posology"));
                    productSolicitation.setStatus(rs.getString("status"));
                    productSolicitation.setTechnicalAnalysis(rs.getString("technical_analysis"));
                    productSolicitation.setReportId(rs.getLong("report_id"));

                    itemList.add(productSolicitation);
                }
            }
        }

        return itemList;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<ForwardingLegacyEntity> findForwardingByLegacy(Long id) throws SQLException {
        long baseId = (id == null ? 0 : id);
        long maxId = baseId + 50_000;
        var forwardingList = new ArrayList<ForwardingLegacyEntity>();

        var sb = """
                SELECT
                e.Id as migrated_id,
                is2.id as product_id,
                is2.Solicitacao_id as solicitation_id,
                p.Nome AS product_name,
                e.Posologia AS posology,
                dv_period.Descricao AS period,
                dv_category.Descricao AS category,
                e.EstoqueIrregular AS irregular_stock,
                e.Quantidade AS quantity,
                dv_forwarding.Descricao AS forwarding,
                us2.Descricao AS unity,
                dv_status.Descricao AS status,
                e.Parecer_id AS report_id,
                e.Oficio_id AS oficio_id
                FROM Encaminhamento e
                LEFT JOIN ItemSolicitacao is2  ON e.Id  = is2.EncaminhamentoControle_id
                LEFT JOIN Produto p  ON p.Id  = is2.Produto_id
                LEFT JOIN FormaOrganizacional fo  ON p.FormaOrganizacional_id = fo.id
                LEFT JOIN DominioValor dv_status ON dv_status.Id = is2.Status_id
                LEFT JOIN DominioValor dv_period ON dv_period.Id = e.Periodo_id
                LEFT JOIN DominioValor dv_category ON dv_category.Id = e.Categoria_id
                LEFT JOIN DominioValor dv_forwarding ON dv_forwarding.Id = e.TipoEncaminhamento_id
                LEFT JOIN UnidadeSaude us2 ON is2.UnidadeSaudeControle_id = us2.Id
                WHERE e.ItemSolicitacao_id IS NOT NULL AND e.Id > ? AND e.Id <= ?
                """;

        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(sb)) {

            ps.setLong(1, baseId);
            ps.setLong(2, maxId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var forwardingLegacy = new ForwardingLegacyEntity();
                    forwardingLegacy.setMigratedId(rs.getLong("migrated_id"));
                    forwardingLegacy.setProductId(rs.getLong("product_id"));
                    forwardingLegacy.setSolicitationId(rs.getLong("solicitation_id"));
                    forwardingLegacy.setProductName(rs.getString("product_name"));
                    forwardingLegacy.setPosology(rs.getString("posology"));
                    forwardingLegacy.setPeriod(rs.getString("period"));
                    forwardingLegacy.setCategory(rs.getString("category"));
                    forwardingLegacy.setIrregularStock(rs.getBoolean("irregular_stock"));
                    forwardingLegacy.setQuantity(rs.getInt("quantity"));
                    forwardingLegacy.setForwarding(rs.getString("forwarding"));
                    forwardingLegacy.setUnity(rs.getString("unity"));
                    forwardingLegacy.setStatus(rs.getString("status"));

                    forwardingList.add(forwardingLegacy);
                }
            }
        }

        return forwardingList;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<OficioLegacyEntity> findOficioByLegacy(Long id) throws SQLException {
        long baseId = (id == null ? 0 : id);
        long maxId = baseId + 50_000;
        var oficioList = new ArrayList<OficioLegacyEntity>();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append("o.Id as migrated_id, ");
        sb.append("o.Solicitacao_id as solicitation_id, ");
        sb.append("o.Numero as number, ");
        sb.append("o.Ano as year ");
        sb.append("FROM Oficio o ");
        sb.append("WHERE o.Solicitacao_id IS NOT NULL AND o.Id > ? AND o.Id <= ?");


        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {

            ps.setLong(1, baseId);
            ps.setLong(2, maxId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var oficioLegacy = new OficioLegacyEntity();
                    oficioLegacy.setMigratedId(rs.getLong("migrated_id"));
                    oficioLegacy.setSolicitationId(rs.getLong("solicitation_id"));
                    oficioLegacy.setNumber(rs.getString("number"));
                    oficioLegacy.setYear(rs.getString("year"));

                    oficioList.add(oficioLegacy);
                }
            }
        }

        return oficioList;
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public List<ReportLegacyEntity> findReportByLegacy(Long id) throws SQLException {
        long baseId = (id == null ? 0 : id);
        long maxId = baseId + 30_000;
        var reportList = new ArrayList<ReportLegacyEntity>();

        var sb = """
                    SELECT
                    p.id as migrated_id,
                    p.Descricao as description,
                    p.DataAnalise as dateAnalysis,
                    u.Nome as name_analyst,
                    dv.Descricao as secretary
                    FROM Parecer p
                    LEFT JOIN UsuarioSistema u ON p.Analista_id = u.Id
                    LEFT JOIN DominioValor dv ON p.Secretaria_id = dv.Id
                    WHERE p.Id > ? AND p.Id <= ?
                """;


        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(sb)) {

            ps.setLong(1, baseId);
            ps.setLong(2, maxId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var reportLegacy = new ReportLegacyEntity();
                    reportLegacy.setMigratedId(rs.getLong("migrated_id"));
                    reportLegacy.setDescription(rs.getString("description"));
                    var dateAnalysis = rs.getTimestamp("dateAnalysis");
                    if (dateAnalysis != null) {
                        reportLegacy.setDateAnalysis(dateAnalysis.toLocalDateTime());
                    }
                    reportLegacy.setNameAnalyst(rs.getString("name_analyst"));
                    reportLegacy.setSecretary(rs.getString("secretary"));
                    reportList.add(reportLegacy);
                }
            }
        }

        return reportList;
    }

    public List<StatusDocumentSolicitationLegacyEntity> findStatusDocumentSolicitationByLegacy(Long id) throws SQLException {
        long baseId = (id == null ? 0 : id);
        long maxId = baseId + 50_000;
        var statusList = new ArrayList<StatusDocumentSolicitationLegacyEntity>();

        var sb = """
                    SELECT
                    ds.Id as migrated_id,
                    ds.Recebido as received,
                    ds.Solicitacao_id as solicitation_id,
                    tp.Descricao as document_type
                    FROM DocumentoSituacao ds
                    LEFT JOIN TipoDocumentoMedico tp ON ds.TipoDocumentoMedico_id = tp.Id
                    WHERE ds.Id > ? AND ds.Id <= ?
                """;


        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(sb)) {

            ps.setLong(1, baseId);
            ps.setLong(2, maxId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var statusDocument = new StatusDocumentSolicitationLegacyEntity();
                    statusDocument.setMigratedId(rs.getLong("migrated_id"));
                    statusDocument.setReceived(rs.getBoolean("received"));
                    statusDocument.setSolicitationId(rs.getLong("solicitation_id"));
                    statusDocument.setDocumentType(rs.getString("document_type"));
                    statusList.add(statusDocument);
                }
            }
        }

        return statusList;
    }

    public List<MedicalDocumentLegacyEntity> findMedicalDocumentByLegacy(Long id) throws SQLException {
        long baseId = (id == null ? 0 : id);
        long maxId = baseId + 30_000;
        var medicalList = new ArrayList<MedicalDocumentLegacyEntity>();

        var sb = """
                    SELECT
                    dm.id AS migrated_id,
                    dm.Solicitacao_id as solicitation_id,
                    dm.DataDocumento as date_created,
                    tdm.Descricao as document_type,
                    u.Descricao as unity,
                    dv.Descricao as bond,
                    pr.nome as responsible_professional,
                    dv_advice.Descricao as advice,
                    dm.ObservacaoDocumento as observation,
                    dm.OutroTipo as other
                    FROM DocumentoMedico dm
                    LEFT JOIN TipoDocumentoMedico tdm  ON tdm.Id  = dm.TipoDocumentoMedico_id
                    LEFT JOIN UnidadeSaude u  ON u.Id  = dm.UnidadeSaude_id
                    LEFT JOIN DominioValor dv ON dv.Id = u.Vinculo_Id
                    LEFT JOIN ProfissionalResponsavel pr ON pr.Id = dm.ProfissionalResponsavel_id
                    LEFT JOIN DominioValor dv_advice ON dv_advice.Id = pr.TipoConselho_id
                    WHERE dm.Id > ? AND dm.Id <= ?
                """;


        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(sb)) {

            ps.setLong(1, baseId);
            ps.setLong(2, maxId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var medicalDocument = new MedicalDocumentLegacyEntity();
                    medicalDocument.setMigratedId(rs.getLong("migrated_id"));
                    medicalDocument.setSolicitationId(rs.getLong("solicitation_id"));
                    var dateCreated = rs.getTimestamp("date_created");
                    if (dateCreated != null) {
                        medicalDocument.setDateCreated(dateCreated.toLocalDateTime());
                    }
                    medicalDocument.setDocumentType(rs.getString("document_type"));
                    medicalDocument.setUnity(rs.getString("unity"));
                    medicalDocument.setBond(rs.getString("bond"));
                    medicalDocument.setResponsibleProfessional(rs.getString("responsible_professional"));
                    medicalDocument.setAdvice(rs.getString("advice"));
                    medicalDocument.setObservation(rs.getString("observation"));
                    medicalDocument.setOther(rs.getString("other"));
                    medicalList.add(medicalDocument);

                }
            }
        }

        return medicalList;
    }

    public List<ProductToMedicalDocumentLegacyEntity> findProductToMedicalDocumentByLegacy(Long id) throws SQLException {
        long baseId = (id == null ? 0 : id);
        long maxId = baseId + 30_000;
        var productToMedicalList = new ArrayList<ProductToMedicalDocumentLegacyEntity>();

        var sb = """
                    SELECT
                    pd.id as migrated_id,
                    pd.DocumentoMedico_id as medical_document_id,
                    pd.Posologia as posology,
                    p.Nome as product_name,
                    dv.Descricao as period
                    FROM ProdutoToDocumento pd
                    LEFT JOIN Produto p ON pd.Produto_id = p.id
                    LEFT JOIN DominioValor dv ON pd.Periodo_id = dv.Id
                    WHERE pd.Id > ? AND pd.Id <= ?
                """;


        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(sb)) {

            ps.setLong(1, baseId);
            ps.setLong(2, maxId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var productToMedical = new ProductToMedicalDocumentLegacyEntity();
                    productToMedical.setMigratedId(rs.getLong("migrated_id"));
                    productToMedical.setMedicalDocumentId(rs.getLong("medical_document_id"));
                    productToMedical.setPosology(rs.getString("posology"));
                    productToMedical.setProductName(rs.getString("product_name"));
                    productToMedical.setPeriod(rs.getString("period"));

                    productToMedicalList.add(productToMedical);
                }
            }
        }

        return productToMedicalList;
    }

    public List<CidToMedicalDocumentLegacyEntity> findCidToMedicalDocumentByLegacy(Long id) throws SQLException {
        long baseId = (id == null ? 0 : id);
        long maxId = baseId + 30_000;
        var cidToMedicalList = new ArrayList<CidToMedicalDocumentLegacyEntity>();

        var sb = """
                    SELECT
                    cd.id as migrated_id,
                    cd.DocumentoMedico_id as medical_document_id,
                    c.Descricao as cid_description,
                    cd.DataDiagnostico as diagnosis_date,
                    cd.DiagnosticoMedico as medical_diagnosis
                    FROM CidToDocumentoMedico cd
                    LEFT JOIN Cid c ON cd.Cid_id = c.Id
                    WHERE cd.Id > ? AND cd.Id <= ?
                """;


        try (Connection conn = databaseConfig.getLegacy().getConnection();
             PreparedStatement ps = conn.prepareStatement(sb)) {

            ps.setLong(1, baseId);
            ps.setLong(2, maxId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var cidToMedical = new CidToMedicalDocumentLegacyEntity();
                    cidToMedical.setMigratedId(rs.getLong("migrated_id"));
                    cidToMedical.setMedicalDocumentId(rs.getLong("medical_document_id"));
                    cidToMedical.setCidDescription(rs.getString("cid_description"));
                    var diagnosisDate = rs.getTimestamp("diagnosis_date");
                    if (diagnosisDate != null) {
                        cidToMedical.setDiagnosisDate(diagnosisDate.toLocalDateTime());
                    }
                    cidToMedical.setMedicalDiagnosis(rs.getString("medical_diagnosis"));

                    cidToMedicalList.add(cidToMedical);
                }
            }
        }

        return cidToMedicalList;
    }
}

