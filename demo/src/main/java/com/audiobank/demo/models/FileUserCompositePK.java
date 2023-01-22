package com.audiobank.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FileUserCompositePK { 
    @Column
    private Long id_file;
    @Column
    private Long id_user;

    public FileUserCompositePK() {

    }

    public FileUserCompositePK(Long id_file, Long id_user) {
        this.id_file = id_file;
        this.id_user = id_user;
    }
}
