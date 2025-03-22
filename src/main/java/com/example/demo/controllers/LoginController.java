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
    public ModelAndView showLogin() {
        ModelAndView modelAndView = new ModelAndView("Login_Page/index.html");
        return modelAndView;
    }

    @PostMapping("login")
    public ModelAndView login(@ModelAttribute LoginRequestDTO request) {
        Optional<User> userOptional = userRepository.findByName(request.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if(passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                System.out.println("Login successful");
                return new ModelAndView("redirect:/");
            } else {
                //Para debug
                System.out.println("Senha errada");
                return new ModelAndView("redirect:/wrong");
            }
        }
        //Para debug
        return new ModelAndView("redirect:/notfound");


    }
}


// receber os valores dos inputs
// Procurar por nome o usuario
// se encontrar username
// usa o Password encoder para verificar se a senha bate com a recebida
// login
// se nao
// Erro
