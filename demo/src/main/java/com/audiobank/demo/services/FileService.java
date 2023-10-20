package com.audiobank.demo.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.audiobank.demo.models.DTOs.AudiofileDTO;

public interface FileService {
    
    Boolean saveFile(AudiofileDTO audiofile, String apiKey) throws IOException;

    Optional<String> getFileExtension(String fileName);
    Optional<String> getFileNameWithoutExtension(String fileName);

    void addTags(List<String> tags, Long audiofileID);
    void addNames(List<String> names, Long audiofileID);

    void deleteFile(Long fileID, String apiKey) throws IOException;

}
