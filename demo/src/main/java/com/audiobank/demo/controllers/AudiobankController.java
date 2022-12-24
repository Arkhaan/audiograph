package com.audiobank.demo.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.audiobank.demo.repositories.AudiofileRepository;
import com.audiobank.demo.models.Audiofile;

@Controller
public class AudiobankController {

    AudiofileRepository repo;

    public AudiobankController(AudiofileRepository repo)
    {
        this.repo = repo;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String helloWorld()
    {
        List<Audiofile> audiofiles = repo.findAll();
        System.out.println(audiofiles.get(0).getFile_name());
        return "main-menu";
    }
    
}
