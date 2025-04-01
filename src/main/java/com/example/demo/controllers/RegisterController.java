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
    public ModelAndView register(@Valid @ModelAttribute("registerRequestDTO") RegisterRequestDTO dto,
                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("Register_Page/index.html", "registerRequestDTO", dto);
        }

        // Verifica username único
        userRepository.findFirstByName(dto.getUsername()).ifPresent(u -> {
            bindingResult.rejectValue("username", "error.username", "Username já está em uso");
        });

        // Verifica email único
        userRepository.findFirstByEmail(dto.getEmail()).ifPresent(u -> {
            bindingResult.rejectValue("email", "error.email", "Email já está em uso");
        });

        if (bindingResult.hasErrors()) {
            return new ModelAndView("Register_Page/index.html", "registerRequestDTO", dto);
        }

        User user = new User();
        user.setName(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        return new ModelAndView("redirect:/login", "success", "Registro realizado com sucesso!");
    }
}





