package com.audiobank.demo.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import com.audiobank.demo.repositories.AudiofileRepository;
import com.audiobank.demo.repositories.TagRepository;
import com.audiobank.demo.services.FileService;
import com.audiobank.demo.services.UserService;
import com.audiobank.demo.models.Audiofile;
import com.audiobank.demo.models.Tag;
import com.audiobank.demo.models.User;

@Controller
@SessionAttributes("apikey")
public class AudiobankController {

    AudiofileRepository audiofileRepo;
    TagRepository tagRepo;
    UserService userService;
    FileService fileService;

    @Value("${files.path}")
    private String filesPath;

    public AudiobankController(AudiofileRepository audiofileRepo,
                                TagRepository tagRepo,
                                UserService userService,
                                FileService fileService)
    {
        this.audiofileRepo = audiofileRepo;
        this.tagRepo = tagRepo;
        this.userService = userService;
        this.fileService = fileService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String main(@RequestParam(name = "tagid", required = false) Long tagID,
                        @RequestParam(name = "userid", required = false) Long userID,
                        Model model)
    {
        if (model.getAttribute("apikey") == null) {
            return "redirect:/login";
        }
        List<Tag> tags = tagRepo.findAll();
        model.addAttribute("tags", tags);
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
        model.addAttribute("filesPath", filesPath);
        List<Audiofile> audiofiles;
        if (tagID != null) {
            audiofiles = audiofileRepo.findAllByTagID(tagID);
        }
        else if (userID != null)
        {
            audiofiles = audiofileRepo.findAllByUserID(userID);
        }
        else {
            audiofiles = audiofileRepo.findAll();
        }
        for ( Audiofile audiofile : audiofiles ) {
            System.out.println(audiofile.getTitle());
        }
        model.addAttribute("audiofiles", audiofiles);
        //System.out.println(audiofiles.get(0).getFull_names());
        //System.out.println(audiofiles.get(0).getTags());
        return "main-menu";
    }

    @RequestMapping("/login")
    public String getLogin() {
        return "login";
    }

    @RequestMapping("/create-account")
    public String getRegister(Model model) {
        List<String> names = userService.getUnclaimedUsers();
        model.addAttribute("fullnames", names);
        return "create-account";
    }

    @PostMapping("/login")
    public String login(Model model, @RequestParam String login, @RequestParam String password) {
        String apiKey = userService.login(login, password);
        if (apiKey == null) {
            model.addAttribute("loginFailed", true);
            return "login";
        }
        model.addAttribute("apikey", apiKey);

        return "redirect:/";
    }

    @PostMapping("/create-account")
    public String createAccount(Model model, @RequestParam String email,
                                @RequestParam String firstName,
                                @RequestParam String lastName) {
        if( userService.createAccount(email, firstName, lastName) ) {
            model.addAttribute("registrationSuccess", true);
            return "login";
        }
        model.addAttribute("registrationFail", true);
        return "create-account";
    }

    @RequestMapping("/logout")
    public String logout(Model model, SessionStatus status) {
        userService.logout(model.getAttribute("apikey").toString());
        status.setComplete();
        model.addAttribute("logoutSuccess", true);
        return "redirect:/login";
    }

    @RequestMapping("/upload-file")
    public String getUploadFile(Model model) {
        List<String> names = userService.getAllFullName();
        model.addAttribute("fullnames", names);
        List<String> categories = tagRepo.findAll().stream().map(tag ->tag.getValue())
                                    .collect(Collectors.toList());
        model.addAttribute("categories", categories);
        return "upload-file";
    }

    @RequestMapping(value = "/upload-file", method = RequestMethod.POST)
    public String uploadFile(Model model, @RequestParam("file") MultipartFile multipartFile,
                                @RequestParam("title") String title,
                                @RequestParam("fullname") String fullname,
                                @RequestParam("category") String category) throws IOException {
        if ( fileService.saveFile(multipartFile, title, fullname, category) ) {
            model.addAttribute("uploadSuccess", true);
        }
        else {
            model.addAttribute("uploadFailed", true);
        }
        return "upload-file";
    }
    
}
