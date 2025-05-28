package com.example.demo.controllers;

import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.models.StatusTask;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repositories.TaskRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;
import org.springframework.security.access.AccessDeniedException;
import com.example.demo.dto.TaskEditRequestDTO;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;


    @GetMapping
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

    @GetMapping("/new")
    public ModelAndView newTask() {
        ModelAndView mv = new ModelAndView("newTask.html");
        mv.addObject("taskRequestDTO", new TaskRequestDTO());
        mv.addObject("listaStatusTask", StatusTask.values());
        return mv;
    }

    @PostMapping
    public ModelAndView saveTask(@Valid @ModelAttribute("taskRequestDTO") TaskRequestDTO taskRequestDTO, BindingResult bindingResult, Authentication authentication) {

        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("newTask.html");
            mv.addObject("listaStatusTask", StatusTask.values());

            List<String> errorMessages = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.toList());
            mv.addObject("errorMessagesForPopup", errorMessages);
            mv.addObject("hasFormErrors", true);

            return mv;
        }

        Optional<User> userOptional = userRepository.findByName(authentication.getName());

        if (userOptional.isEmpty()) {
            ModelAndView mv = new ModelAndView("newTask.html");
            mv.addObject("taskRequestDTO", taskRequestDTO);
            mv.addObject("listaStatusTask", StatusTask.values());
            mv.addObject("generalErrorForPopup", "Erro: Usuário não autenticado ou não encontrado. Por favor, tente fazer login novamente.");
            mv.addObject("hasFormErrors", true);
            return mv;
        }
        User user = userOptional.get();

        Task task = taskRequestDTO.toTask();
        task.setUser(user);

        taskRepository.save(task);

        return new ModelAndView("redirect:/tasks");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        checkTaskOwnership(task);

        ModelAndView mv = new ModelAndView("/Edit_Page/index");
        mv.addObject("taskEditRequestDTO", TaskEditRequestDTO.fromTask(task));
        mv.addObject("listaStatusTask", StatusTask.values());
        return mv;
    }

    @PostMapping("/edit")
    public ModelAndView updateTask(@Valid TaskEditRequestDTO taskEditRequestDTO, BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView("Edit_Page/index")
                    .addObject("listaStatusTask", StatusTask.values());
        }

        Task task = taskRepository.findById(taskEditRequestDTO.getIdTask())
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        checkTaskOwnership(task);

        task.setTitle(taskEditRequestDTO.getTitle());
        task.setContent(taskEditRequestDTO.getContent());
        task.setStatusTask(taskEditRequestDTO.getStatusTask());

        taskRepository.save(task);
        return new ModelAndView("redirect:/tasks");
    }


    @GetMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable Long id) {
        // Obtém a autenticação do usuário atual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Obtém o nome de usuário

        ModelAndView mv = new ModelAndView("redirect:/tasks");

        // Busca a task verificando se pertence ao usuário
        Optional<Task> optional = taskRepository.findByIdTaskAndUserName(id, username);

        if (optional.isPresent()) {
            taskRepository.deleteById(id);
            mv.addObject("mensagem", "Task #" + id + " deletada com sucesso!");
            mv.addObject("error", false);
        } else {
            // Não encontrou ou não pertence ao usuário
            mv.addObject("mensagem", "Erro: Task #" + id + " não encontrada ou você não tem permissão!");
            mv.addObject("error", true);
        }

        return mv;
    }

    private void checkTaskOwnership(Task task) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!task.getUser().getName().equals(currentUsername)) {
            throw new AccessDeniedException("Acesso negado");
        }
    }
}