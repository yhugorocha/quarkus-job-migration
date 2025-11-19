package com.extreme.app.services;

import com.extreme.app.entities.enums.StoragePath;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {
    String upload(FileUpload fileUpload, StoragePath storagePath) throws IOException;
    String upload(byte[] content, String contentType, StoragePath storagePath) throws IOException;
    InputStream download(String path) throws IOException;
    void delete(String path) throws IOException;
}