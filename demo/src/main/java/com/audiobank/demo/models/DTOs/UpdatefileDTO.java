package com.audiobank.demo.models.DTOs;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatefileDTO {

    private String title;
    private String description;
    private List<String> names;
    private List<String> tags = new ArrayList<String>();

    public UpdatefileDTO() {
    }
}