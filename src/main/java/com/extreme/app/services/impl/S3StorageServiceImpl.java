package com.extreme.app.services.impl;

import com.extreme.app.entities.enums.StoragePath;
import com.extreme.app.services.StorageService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class S3StorageServiceImpl implements StorageService {

    @ConfigProperty(name = "storage.bucket-name", defaultValue = "crls-service-dev")
    String bucketName;

    private final S3Client s3Client;

    @Override
    public String upload(FileUpload fileUpload, StoragePath storagePath) throws IOException {
        try {
            byte[] content = Files.readAllBytes(fileUpload.uploadedFile());

            String key = storagePath.getPath().concat(UUID.randomUUID().toString());

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(fileUpload.contentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(content));

            return key;

        } catch (IOException | S3Exception e) {
            log.error("Não foi possível salvar o arquivo no S3. arquivo:{}", fileUpload.fileName());
            throw new IOException("Não foi possível salvar o arquivo no S3.");
        }
    }

    @Override
    public String upload(byte[] content, String contentType, StoragePath storagePath) throws IOException {
        try {
            String key = storagePath.getPath().concat(UUID.randomUUID().toString());

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(content));
            return key;
        } catch (S3Exception e) {
            log.error("Não foi possível salvar o arquivo no S3.", e);
            throw new IOException("Não foi possível salvar o arquivo no S3.");
        }
    }

    @Override
    public InputStream download(String key) throws IOException {
        try{
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            return s3Client.getObjectAsBytes(request).asInputStream();
        } catch (S3Exception e) {
            log.error("Não foi possível buscar o arquivo no S3. key:{}", key);
            throw new IOException("Não foi possível buscar o arquivo no S3.");
        }
    }

    @Override
    public void delete(String key) throws IOException {
        try{
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
        } catch (S3Exception e) {
            log.error("Não foi possível apagar o arquivo no S3. key:{}", key);
            throw new IOException("Não foi possível buscar o arquivo no S3.");
        }
    }

}
