package com.audiobank.demo.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.audiobank.demo.models.Audiofile;
import com.audiobank.demo.models.FileTag;
import com.audiobank.demo.models.FileUser;
import com.audiobank.demo.models.Tag;
import com.audiobank.demo.models.User;
import com.audiobank.demo.models.DTOs.AudiofileDTO;
import com.audiobank.demo.repositories.AudiofileRepository;
import com.audiobank.demo.repositories.FileTagRepository;
import com.audiobank.demo.repositories.FileUserRepository;
import com.audiobank.demo.repositories.TagRepository;
import com.audiobank.demo.repositories.UserRepository;
import com.jayway.jsonpath.Option;

@Service
public class FileServiceImpl implements FileService {

    @Value("${files.fullpath}")
    private String filesFullpath;

    AudiofileRepository audiofileRepo;
    TagRepository tagRepo;
    FileTagRepository fileTagRepo;
    FileUserRepository fileUserRepo;
    UserRepository userRepo;

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
    }
    
    @Override
    public Boolean saveFile(AudiofileDTO audiofileDTO, String apiKey) throws IOException {
        Optional<String> extension = getFileExtension(audiofileDTO.getMultipartFile().getOriginalFilename());
        if ( extension.isPresent() && ( extension.get().equals("mp3") || extension.get().equals("wav") ) ) {
            Audiofile audiofile = new Audiofile(audiofileDTO.getTitle());
            audiofile.setDescription(audiofileDTO.getDescription());
            Long uploader = userRepo.findByApiKey(apiKey).get().getId();
            audiofile.setUploader(uploader);
            audiofileRepo.save(audiofile);
            String filename = audiofile.getFile_id() + java.util.UUID.randomUUID().toString() + "." + extension.get();
            audiofileDTO.getMultipartFile().transferTo(new File(filesFullpath + filename));
            audiofile.setFileName(filename);
            audiofileRepo.save(audiofile);
            audiofileDTO.convertNewNames();
            audiofileDTO.convertNewTags();
            addTags(audiofileDTO.getTags(), audiofile.getFile_id());
            addNames(audiofileDTO.getNames(), audiofile.getFile_id());
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

}
