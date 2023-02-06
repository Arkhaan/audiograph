package com.audiobank.demo.models;

import lombok.Getter;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Getter
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String first_name;
    private String last_name;
    private LocalDate join_date;
    private String password;
    private String apikey;

    public User() {
    }

    public User(String email, String password, String firstName, String lasName) {
        this.email = email;
        this.password = password;
        this.first_name = firstName;
        this.last_name = lasName;
        this.join_date = java.time.LocalDate.now();
    }

    public User(String firstName, String lastName) {
        this.first_name = firstName;
        this.last_name = lastName;
    }

    public void setApiKey(String apiKey) {
        this.apikey = apiKey;
    }

    public String getFullName() {
        return this.first_name + ' ' + this.last_name;
    }
}