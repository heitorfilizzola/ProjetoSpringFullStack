package com.example.demo.controllers;

import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("login")

    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("Login_Page/index.html");
        return modelAndView;
    }
}
