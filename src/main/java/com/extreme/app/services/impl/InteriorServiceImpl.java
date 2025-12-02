package com.extreme.app.services.impl;

import com.extreme.app.dto.AssistedInterior;
import com.extreme.app.dto.DataAssistedInterior;
import com.extreme.app.entities.AssistedEntity;
import com.extreme.app.repository.AssistedRepository;
import com.extreme.app.services.InteriorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class InteriorServiceImpl implements InteriorService {

    private final AssistedRepository assistedRepository;
    private final ObjectMapper objectMapper;
    private final AtomicInteger success = new AtomicInteger(0);
    private final AtomicInteger duplicated = new AtomicInteger(0);
    private final AtomicInteger duplicatedCad = new AtomicInteger(0);
    private final AtomicInteger duplicatedNotCad = new AtomicInteger(0);
    private final AtomicInteger error = new AtomicInteger(0);

    List<AssistedEntity> assistedCache = new ArrayList<>();

    @Override
    public void importAssistedInterior(String archivePath) throws Exception {

        try {

            InputStream inputStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(archivePath);

            if (inputStream == null) {
                throw new NotFoundException("Arquivo não encontrado: " + archivePath);
            }

            var dataAssisted = objectMapper.readValue(inputStream, DataAssistedInterior.class);

            var entities = dataAssisted.getData().stream()
                    .map(AssistedInterior::toEntity)
                    .toList();

            entities.forEach(this::saveAssisted);
            this.saveCache();
            log.info("=== RESULTADO DA MIGRAÇÃO ASSISTIDOS ===");
            log.info("Total: {}", entities.size());
            log.info("Sucesso: {}", success);
            log.info("Duplicados: {}", duplicated);
            log.info("Duplicados cadastrados: {}", duplicatedCad);
            log.info("Duplicados não cadastrados: {}", duplicatedNotCad);
            log.info("Erros: {}", error);

        } catch (Exception e) {
            log.error("Erro ao importar Assistidos: {}", e.getMessage(), e);
            throw new Exception("Erro ao importar Assistidos: " + e.getMessage());
        }
    }

    @Transactional
    public void saveAssisted(AssistedEntity assisted){
        try{
            assistedRepository.persist(assisted);
            success.incrementAndGet();
        } catch (Exception e) {
            if(e.getMessage().contains("UQ__assisted__")){
                duplicated.incrementAndGet();
                log.error("Assistido com cpf já cadastrado:{}",assisted.getCpf());
                log.error("Assistido adicionado no cache");
                assistedCache.add(assisted);
            }else{
                error.incrementAndGet();
                log.error("Erro ao cadastrar assistido com cpf:{}",assisted.getCpf());
            }
        }
    }

    @Transactional
    public void saveCache(){
        assistedCache.forEach(a -> {
           var assisted = assistedRepository.findByCpf(a.getCpf());
           if(assisted.isPresent()){
               assisted.get().setMigratedInteriorId(a.getMigratedInteriorId());
               assistedRepository.persist(assisted.get());
               duplicatedCad.incrementAndGet();
           }else{
               duplicatedNotCad.incrementAndGet();
           }
        });
    }
}
