package com.audiobank.demo.services;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.audiobank.demo.models.Audiofile;
import com.audiobank.demo.repositories.AudiofileRepository;

@Service
public class FileServiceImpl implements FileService {

    @Value("${files.fullpath}")
    private String filesFullpath;

    AudiofileRepository audiofileRepo;

    public FileServiceImpl(AudiofileRepository audiofileRepo) {
        this.audiofileRepo = audiofileRepo;
    }
    
    @Override
    public Boolean saveFile(MultipartFile file, String title, String fullname, String category) throws IOException {
        Optional<String> extension = getFileExtension(file.getOriginalFilename());
        if ( extension.isPresent() && ( extension.get().equals("mp3") || extension.get().equals("wav") ) ) {
            Audiofile audiofile = new Audiofile(title);
            audiofileRepo.save(audiofile);
            String filename = audiofile.getFile_id() + java.util.UUID.randomUUID().toString() + "." + extension.get();
            file.transferTo(new File(filesFullpath + filename));
            audiofile.setFileName(filename);
            audiofileRepo.save(audiofile);
            return true;
        }
        return false;
    }

    @Override
    public Optional<String> getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
    }

}
