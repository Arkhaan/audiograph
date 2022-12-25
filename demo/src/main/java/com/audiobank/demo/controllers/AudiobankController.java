package com.audiobank.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import com.audiobank.demo.repositories.AudiofileRepository;
import com.audiobank.demo.models.Audiofile;

@Controller
public class AudiobankController {

    AudiofileRepository repo;

    @Value("${files.path}")
    private String filesPath;

    public AudiobankController(AudiofileRepository repo)
    {
        this.repo = repo;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String helloWorld(Model model)
    {
        model.addAttribute("filesPath", filesPath);
        List<Audiofile> audiofiles = repo.findAll();
        model.addAttribute("audiofiles", audiofiles);
        //System.out.println(audiofiles.get(0).getFile_name());
        //System.out.println(filesPath);
        return "main-menu";
    }
    
}
