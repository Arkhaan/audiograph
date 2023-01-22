package com.audiobank.demo.models;

import lombok.Getter;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Getter
@Entity
@Table(name="fileusers")
public class FileUser {

    @EmbeddedId
    private FileUserCompositePK pk;

    public FileUser() {
        
    }

    public FileUser(Long id_file, Long id_user) {
        this.pk = new FileUserCompositePK(id_file, id_user);
    }
}