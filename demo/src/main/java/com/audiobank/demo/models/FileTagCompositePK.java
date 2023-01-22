package com.audiobank.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FileTagCompositePK { 
    @Column
    private Long id_file;
    @Column
    private Long id_tag;

    public FileTagCompositePK() {

    }

    public FileTagCompositePK(Long id_file, Long id_tag) {
        this.id_file = id_file;
        this.id_tag = id_tag;
    }
}
