package com.audiobank.demo.models;

import lombok.Getter;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Getter
@Entity
@Table(name="audiofiles")
public class Audiofile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long file_id;
    private String file_name;
    private LocalDate date;
    private Long uploader;
    private String title;
    private String description;
    private String file_format;
    @Column(insertable = false, updatable = false)
    private String full_names;
    @Column(insertable = false, updatable = false)
    private String tags;

    public Audiofile() {
    }

    public Audiofile(String title) {
        this.title = title;
        this.date = java.time.LocalDate.now();
    }

    public void setFileName(String filename) {
        this.file_name = filename;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUploader(Long uploader) {
        this.uploader = uploader;
    }

    public void setFileFormat(String format) {
        this.file_format = format;
    }
}