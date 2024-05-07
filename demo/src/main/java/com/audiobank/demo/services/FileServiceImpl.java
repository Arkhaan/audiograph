package com.audiobank.demo.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileStore;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.audiobank.demo.models.Audiofile;
import com.audiobank.demo.models.FileTag;
import com.audiobank.demo.models.FileUser;
import com.audiobank.demo.models.Tag;
import com.audiobank.demo.models.User;
import com.audiobank.demo.models.DTOs.AudiofileDTO;
import com.audiobank.demo.models.DTOs.UpdatefileDTO;
import com.audiobank.demo.repositories.AudiofileRepository;
import com.audiobank.demo.repositories.FileTagRepository;
import com.audiobank.demo.repositories.FileUserRepository;
import com.audiobank.demo.repositories.TagRepository;
import com.audiobank.demo.repositories.UserRepository;

@Service
public class FileServiceImpl implements FileService {

    @Value("${files.fullpath}")
    private String filesFullpath;
    @Value("${files.original}")
    private String originalPath;
    @Value("${files.converted}")
    private String convertedPath;

    AudiofileRepository audiofileRepo;
    TagRepository tagRepo;
    FileTagRepository fileTagRepo;
    FileUserRepository fileUserRepo;
    UserRepository userRepo;

    //List<String> supportedFormats;

    public FileServiceImpl(AudiofileRepository audiofileRepo,
                            TagRepository tagRepo,
                            FileTagRepository fileTagRepo,
                            UserRepository userRepo,
                            FileUserRepository fileUserRepo) {
        this.audiofileRepo = audiofileRepo;
        this.tagRepo = tagRepo;
        this.fileTagRepo = fileTagRepo;
        this.userRepo = userRepo;
        this.fileUserRepo = fileUserRepo;
        //this.supportedFormats = Arrays.asList("mp3", "wav", "m4a", "adts");
    }
    
    @Override
    public Boolean saveFile(AudiofileDTO audiofileDTO, String apiKey) throws IOException {
        Optional<String> extension = getFileExtension(audiofileDTO.getMultipartFile().getOriginalFilename());
        // First check if there is enough space on the disk for the file + conversion:
        File root = new File("/");
        long usableSpace = root.getUsableSpace();
        long fileSize = audiofileDTO.getMultipartFile().getSize();
        System.out.println("Usable space: " + usableSpace);
        System.out.println("File size: " + fileSize);
        if (usableSpace < (fileSize * 10)) {
            return false;
        }
        if ( extension.isPresent() ) { //&& supportedFormats.contains(extension.get()) ) {
            // Create audiofile
            Audiofile audiofile = new Audiofile(audiofileDTO.getTitle());
            audiofile.setDescription(audiofileDTO.getDescription());
            Long uploader = userRepo.findByApiKey(apiKey).get().getId();
            audiofile.setUploader(uploader);
            audiofile.setFileFormat(extension.get());
            audiofileRepo.save(audiofile);
            // Save and convert file
            String originalFilename = audiofile.getFile_id() + java.util.UUID.randomUUID().toString() + "." + extension.get();
            String convertedFilename = getFileNameWithoutExtension(originalFilename).get() + ".wav";
            String originalFilePath = filesFullpath + originalPath + originalFilename;
            String convertedFilePath = filesFullpath + convertedPath + convertedFilename;
            audiofileDTO.getMultipartFile().transferTo(new File(originalFilePath));
            Process process = Runtime.getRuntime().exec("ffmpeg -i " + originalFilePath + " " + convertedFilePath);
            try {
                process.waitFor();
                if (process.exitValue() != 0) {
                    System.out.println("ffmpeg failed to process the file " + originalFilePath);
                    return false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            //Finalize audiofile
            audiofile.setFileName(originalFilename);
            audiofileRepo.save(audiofile);
            addTags(audiofileDTO.getTags(), audiofile.getFile_id());
            addNames(audiofileDTO.getNames(), audiofile.getFile_id());
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateFile(UpdatefileDTO audiofileDTO, Long fileID, String apiKey) throws IOException {
        // get file from database and update it
        Optional<Audiofile> audiofileOpt = audiofileRepo.findByID(fileID);
        if (!audiofileOpt.isPresent()) {
            return false;
        }
        Audiofile audiofile = audiofileOpt.get();
        audiofile.setTitle(audiofileDTO.getTitle());
        audiofile.setDescription(audiofileDTO.getDescription());
        audiofileRepo.save(audiofile);
        // Add tags and names
        fileTagRepo.deleteEntry(fileID);
        fileUserRepo.deleteEntry(fileID);
        addTags(audiofileDTO.getTags(), fileID);
        addNames(audiofileDTO.getNames(), fileID);
        return true;
    }

    @Override
    public Optional<String> getFileNameWithoutExtension(String fileName) {
        return Optional.ofNullable(fileName)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(0, fileName.lastIndexOf(".")));
    }

    @Override
    public Optional<String> getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
            .filter(f -> f.contains("."))
            .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
    }

    @Override
    public void addTags(List<String> tags, Long audiofileID) {
        if (tags.isEmpty()) tags.add("Non catégorisé");
        for (String value: tags) {
            Optional<Tag> tag = tagRepo.getByValue(value);
            if (!tag.isPresent()) {
                Tag newTag = new Tag(value);
                tagRepo.save(newTag);
                tag = tagRepo.getByValue(value);
            }
            FileTag ft = new FileTag(audiofileID, tag.get().getId());
            fileTagRepo.save(ft);
        }
    }

    public void addNames(List<String> names, Long audiofileID) {
        for (String value: names) {
            if (value.equals("new-names")) {
                continue;
            }
            String[] name = value.split(" ");
            Optional<User> user = userRepo.findByFullName(name[0], name[1]);
            if (!user.isPresent()) {
                User newUser = new User(name[0], name[1]);
                userRepo.save(newUser);
                user = userRepo.findByFullName(name[0], name[1]);
            }
            FileUser ft = new FileUser(audiofileID, user.get().getId());
            fileUserRepo.save(ft);
        }
    }

    public void deleteFile(Long fileID, String apiKey) throws IOException {
        String filename = audiofileRepo.getFileName(fileID);
        audiofileRepo.deleteFile(fileID, userRepo.findByApiKey(apiKey).get().getId());
        fileTagRepo.deleteEntry(fileID);
        fileUserRepo.deleteEntry(fileID);
        Path fileToDeletePath = Paths.get(filesFullpath + convertedPath + getFileNameWithoutExtension(filename).get() + ".wav");
        Files.delete(fileToDeletePath);
        fileToDeletePath = Paths.get(filesFullpath + originalPath + filename);
        Files.delete(fileToDeletePath);
    }



}
