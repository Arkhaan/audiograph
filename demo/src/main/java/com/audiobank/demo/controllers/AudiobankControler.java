package com.audiobank.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AudiobankControler {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String helloWorld()
    {
        return "main-menu";
    }
    
}
