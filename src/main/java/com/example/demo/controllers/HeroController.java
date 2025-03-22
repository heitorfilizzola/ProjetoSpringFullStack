package com.example.demo.controllers;

import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HeroController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public ModelAndView mainPage() {
        ModelAndView modelAndView = new ModelAndView("index.html");
        return modelAndView;
    }
}
