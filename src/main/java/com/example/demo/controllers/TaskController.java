package com.example.demo.controllers;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class TaskController {
    @Autowired
    private UserRepository userRepository;

//    @GetMapping("")
//    public ModelAndView index() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
//        Optional<User> userOptional = userRepository.findByName(username);
//        User user = userOptional.get();
//        String usuarioId = user.getId();
//        return new ModelAndView("redirect:/" + usuarioId);
//    }
//
//    @GetMapping("/{uId}")
//    public ModelAndView mainPage(@PathVariable String uId) {
//        Optional<User> userOptional = userRepository.findById(uId);
//
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            ModelAndView mv = new ModelAndView("index.html");
//            mv.addObject("user", user);
//            mv.addObject("tasks", user.getTasks());
//            return mv;
//        }else {
//            ModelAndView mv = new ModelAndView("404.html");
//            return mv;
//        }
//    }
//


    @GetMapping("/tasks")
    public ModelAndView mainPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOptional = userRepository.findByName(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            ModelAndView mv = new ModelAndView("index.html");
            mv.addObject("user", user);
            mv.addObject("tasks", user.getTasks());
            return mv;
        } else {
            return new ModelAndView("404.html");
        }
    }

    @GetMapping("/tasks/new")
    public ModelAndView newTask( TaskRequestDTO taskRequestDTO) {
        ModelAndView mv = new ModelAndView("newTask.html");
        return mv;
    }

    @PostMapping("/tasks")
    public ModelAndView saveTask(TaskRequestDTO taskRequestDTO) {
        Task task = taskRequestDTO.toTask();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOptional = userRepository.findByName(username);

        User user = userOptional.get();

        task.setUser(user);

        user.getTasks().add(task);
        userRepository.save(user);

        return new ModelAndView("redirect:/tasks");
    }

}
