package com.audiobank.demo.models.DTOs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AudiofileDTO {

    MultipartFile multipartFile;
    private String title;
    private String description;
    private List<String> names;
    private List<String> tags = new ArrayList<String>();

    public AudiofileDTO() {
    }
}