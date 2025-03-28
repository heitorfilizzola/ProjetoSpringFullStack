package com.example.demo.controllers;

import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegisterController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("register")
    public ModelAndView showRegister() {
        ModelAndView mv = new ModelAndView("Register_Page/index.html");
        mv.addObject("registerRequestDTO", new RegisterRequestDTO());
        return mv;

    }

    @PostMapping("register")
    public ModelAndView register(@Valid @ModelAttribute RegisterRequestDTO registerRequestDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            System.out.println("Erros de validação: " + bindingResult.getAllErrors());
            ModelAndView mv = new ModelAndView("Register_Page/index.html");
            mv.addObject("error", bindingResult.getAllErrors());
            return mv;
        }

        String passwordEncripted = passwordEncoder.encode(registerRequestDTO.getPassword());

        User user = registerRequestDTO.toEntity();

        user.setName(user.getName());
        user.setPassword(passwordEncripted);
        user.setEmail(registerRequestDTO.getEmail());
        userRepository.save(user);

        ModelAndView mv = new ModelAndView("Login_Page/index.html");

        return mv;
    }
}






