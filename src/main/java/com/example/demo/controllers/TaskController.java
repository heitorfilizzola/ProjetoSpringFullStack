package com.example.demo.controllers;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.models.StatusTask;
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
        mv.addObject("task", new TaskRequestDTO());
        mv.addObject("listaStatusTask", StatusTask.values());
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
