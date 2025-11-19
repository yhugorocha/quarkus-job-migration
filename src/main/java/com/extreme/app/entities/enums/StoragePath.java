package com.extreme.app.entities.enums;

import lombok.Getter;

@Getter
public enum StoragePath {

    ASSISTED("assistidos/"),
    TECHNICAL_REPORT("pareceres/"),
    SOLICITATION("solicitacoes/");

    private final String path;

    StoragePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}