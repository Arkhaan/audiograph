package com.audiobank.demo.services;

import java.io.IOException;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    
    Boolean saveFile(MultipartFile file, String title, String fullname, String category) throws IOException;

    Optional<String> getFileExtension(String fileName);

}
