package com.audiobank.demo.models.DTOs;

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
    private List<String> tags;
    private String newNames;
    private String newTags;

    public AudiofileDTO() {
    }

    public void convertNewNames() {
        if ( ! newNames.isEmpty() ) {
            for (String name : newNames.split(",", 0) ) {
                names.add(name.strip());
            }
        }
    }

    public void convertNewTags() {
        if ( ! newTags.isEmpty() ) {
            for (String tag : newTags.split(",", 0) ) {
                tags.add(tag.strip());
            }
        }
    }
}