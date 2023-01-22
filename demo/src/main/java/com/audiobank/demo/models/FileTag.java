package com.audiobank.demo.models;

import lombok.Getter;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Getter
@Entity
@Table(name="filetags")
public class FileTag {

    @EmbeddedId
    private FileTagCompositePK pk;

    public FileTag() {
        
    }

    public FileTag(Long id_file, Long id_tag) {
        this.pk = new FileTagCompositePK(id_file, id_tag);
    }
}