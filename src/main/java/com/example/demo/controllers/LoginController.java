package com.example.demo.controllers;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("login")
    public ModelAndView showLogin(
            @RequestParam(value = "error", required = false) String error) {

        ModelAndView mv = new ModelAndView("Login_Page/index.html");
        mv.addObject("loginRequestDTO", new LoginRequestDTO());

        if (error != null) {
            mv.addObject("error", "Usuário ou senha inválidos");
        }

        return mv;
    }

    @PostMapping("login")
    public ModelAndView login(@ModelAttribute LoginRequestDTO request) {
        Optional<User> userOptional = userRepository.findByName(request.getUsername());

        if (userOptional.isEmpty()) {
            ModelAndView mv = new ModelAndView("Login_Page/index.html");
            mv.addObject("error", "Usuário não encontrado");
            mv.addObject("loginRequestDTO", request);
            return mv;
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            ModelAndView mv = new ModelAndView("Login_Page/index.html");
            mv.addObject("error", "Senha incorreta");
            mv.addObject("loginRequestDTO", request);
            return mv;
        }

        return new ModelAndView("redirect:/tasks");
    }
}
