package com.audiobank.demo.models;

import lombok.Getter;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Getter
@Entity
public class Audiofile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long file_id;
    private String file_name;
    private Date date;
    private String uploader;

    public Audiofile() {
    }

    public Audiofile(String name) {
        this.file_name = name;
    }
}