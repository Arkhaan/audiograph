package com.audiobank.demo.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import com.audiobank.demo.repositories.AudiofileRepository;
import com.audiobank.demo.repositories.TagRepository;
import com.audiobank.demo.services.FileService;
import com.audiobank.demo.services.UserService;
import com.audiobank.demo.models.Audiofile;
import com.audiobank.demo.models.Tag;
import com.audiobank.demo.models.User;
import com.audiobank.demo.models.DTOs.AudiofileDTO;
import com.audiobank.demo.models.DTOs.UpdatefileDTO;

@Controller
@SessionAttributes("apikey")
public class AudiobankController {

    AudiofileRepository audiofileRepo;
    TagRepository tagRepo;
    UserService userService;
    FileService fileService;

    @Value("${files.path}${files.converted}")
    private String filesPath;

    @Value("${invitation.code}")
    private String invitationCode;

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

    public void populateSidebar(Model model)
    {
        List<Tag> tags = tagRepo.findAll();
        model.addAttribute("tags", tags);
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
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
                                @RequestParam String lastName,
                                @RequestParam String inviteCode) {
        if (!inviteCode.equals(invitationCode)) {
            model.addAttribute("wrongCode", true);
            return "create-account";
        }
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

    public void populateUploadPage(Model model) {
        List<String> names = userService.getAllFullName();
        model.addAttribute("fullnames", names);
        List<String> categories = tagRepo.findAll().stream().map(tag ->tag.getValue())
                                    .collect(Collectors.toList());
        model.addAttribute("categories", categories);
        model.addAttribute("audiofile", new AudiofileDTO());
    }

    @RequestMapping("/upload-file")
    public String getUploadFile(Model model) {
        if (model.getAttribute("apikey") == null) {
            return "redirect:/login";
        }
        populateSidebar(model);
        populateUploadPage(model);
        return "upload-file";
    }

    @RequestMapping(value = "/upload-file", method = RequestMethod.POST)
    public String uploadFile(Model model, @ModelAttribute("audiofile") AudiofileDTO audiofile) throws IOException {
        String apiKey = model.getAttribute("apikey").toString();
        if ( fileService.saveFile(audiofile, apiKey) ) {
            model.addAttribute("uploadSuccess", true);
        }
        else {
            model.addAttribute("uploadFailed", true);
        }

        populateSidebar(model);
        populateUploadPage(model);
        
        return "upload-file";
    }

    @RequestMapping(value = "/my-files")
    public String getMyFiles(Model model) {
        if ( model.getAttribute("apikey") == null) {
            return "redirect:/login";
        }
        populateSidebar(model);

        model.addAttribute("filesPath", filesPath);
        List<Audiofile> audiofiles;
        audiofiles = audiofileRepo.findAllByUploaderID(userService.getUserId(model.getAttribute("apikey").toString()));
        model.addAttribute("audiofiles", audiofiles);
        return "my-files";
    }

    @RequestMapping(value = "/delete-file")
    public String deleteFile(Model model, @RequestParam(name = "fileid") Long fileID) throws IOException {
        if ( model.getAttribute("apikey") == null) {
            return "redirect:/login";
        }
        fileService.deleteFile(fileID, model.getAttribute("apikey").toString());
        return "redirect:/my-files";
    }

    public void populateEditPage(Model model, Long fileID) {
        // get file title
        String title = audiofileRepo.getTitle(fileID);
        model.addAttribute("currentTitle", title);
        // get file description
        String description = audiofileRepo.getDescription(fileID);
        model.addAttribute("currentDescription", description);
        // get file speaker names
        List<String> names = audiofileRepo.getNames(fileID);
        model.addAttribute("names", names);
        // get file tags
        List<String> currentTags = audiofileRepo.getTags(fileID);
        model.addAttribute("currentTags", currentTags);
        // Create empty object
        model.addAttribute("audiofile", new UpdatefileDTO());
        model.addAttribute("fileid", fileID);
    }

    @RequestMapping(value = "/edit-file")
    public String editFileRequest(Model model, @RequestParam(name = "fileid") Long fileID) {
        if ( model.getAttribute("apikey") == null) {
            return "redirect:/login";
        }
        populateSidebar(model);
        populateEditPage(model, fileID);
        
        return "edit-file";
    }

    @RequestMapping(value = "/edit-file", method = RequestMethod.POST)
    public String updateFile(Model model, @RequestParam(name = "fileid") Long fileID, @ModelAttribute("audiofile") UpdatefileDTO audiofile) throws IOException {
        String apiKey = model.getAttribute("apikey").toString();
        if ( fileService.updateFile(audiofile, fileID, apiKey) ) {
            model.addAttribute("updateSuccess", true);
        }
        else {
            model.addAttribute("updateFailed", true);
        }
        populateSidebar(model);
        populateEditPage(model, fileID);
        return "edit-file";
    }
    
}
