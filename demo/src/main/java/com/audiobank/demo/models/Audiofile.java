package com.audiobank.demo.models;

import lombok.Getter;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Getter
@Entity
public class Audiofile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long file_id;
    private String file_name;
    private Date date;
    private String uploader;
    private String title;
    private String description;
    @Transient
    private String full_names;
    @Transient
    private String tags;

    public Audiofile() {
    }

    public Audiofile(String title) {
        this.title = title;
    }

    public void setFileName(String filename) {
        this.file_name = filename;
    }
}