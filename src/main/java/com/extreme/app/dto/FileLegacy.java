package com.extreme.app.dto;

import com.extreme.app.entities.AssistedAttachmentEntity;
import com.extreme.app.entities.AssistedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileLegacy {

    private Long id;
    private Long assistedId;
    private Long solicitationId;
    private String fileName;
    private byte[] data;

    public AssistedAttachmentEntity toEntity(AssistedEntity assisted) {
        AssistedAttachmentEntity entity = new AssistedAttachmentEntity();
        entity.setMigratedId(this.id);
        entity.setName(this.fileName);
        entity.setAssisted(assisted);
        entity.setDateCreated(LocalDateTime.now());
        return entity;
    }
}
