package com.example.demo.controllers;

import com.example.demo.dto.ForgotPasswordRequestDTO;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/forgotpassword")
    public ModelAndView showForgotPasswordPage() {
        ModelAndView mv = new ModelAndView("ForgotPassword_Page/index");
        mv.addObject("forgotPasswordRequestDTO", new ForgotPasswordRequestDTO());
        return mv;
    }

    @PostMapping("forgotpassword")
    public ModelAndView processForgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        ModelAndView mv = new ModelAndView("ForgotPassword_Page/index");

        Optional<User> userOptional = userRepository.findByEmail(forgotPasswordRequestDTO.getEmail());
        if (userOptional.isEmpty()) {
            mv.addObject("error", "E-mail não encontrado!");
            return mv;
        }

        mv.addObject("message", "Um link de recuperação foi enviado para o seu e-mail.");
        return mv;
    }
}