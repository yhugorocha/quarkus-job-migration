package com.extreme.app.entities.enums;

import jakarta.ws.rs.NotSupportedException;
import lombok.Getter;

@Getter
public enum AttachmentType {
    PDF("application/pdf"),
    JPEG("image/jpeg"),
    JPG("image/jpg"),
    DOC("application/msword"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    private final String label;

    AttachmentType(String label) {
        this.label = label;
    }

    public static AttachmentType fromLabel(String label) throws NotSupportedException {
        for (AttachmentType type : AttachmentType.values()) {
            if (type.getLabel().equalsIgnoreCase(label.trim())) {
                return type;
            }
        }
        throw new NotSupportedException("Tipo de Arquivo desconhecida: " + label);
    }
}
