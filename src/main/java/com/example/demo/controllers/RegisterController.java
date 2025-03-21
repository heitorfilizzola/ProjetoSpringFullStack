package com.example.demo.controllers;

import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegisterController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView("Register_Page/index.html");
        return modelAndView;

    }
}
