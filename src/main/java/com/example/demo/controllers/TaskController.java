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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;
import org.springframework.security.access.AccessDeniedException;
import com.example.demo.dto.TaskEditRequestDTO;
import com.example.demo.dto.TaskRequestDTO;

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
    public ModelAndView newTask(TaskRequestDTO taskRequestDTO) {
        ModelAndView mv = new ModelAndView("newTask.html");
        mv.addObject("task", new TaskRequestDTO());
        mv.addObject("listaStatusTask", StatusTask.values());
        return mv;
    }

    @PostMapping
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
    private ModelAndView delete(@PathVariable Long id) {
        Optional<Task> optional = this.taskRepository.findById(id);

        ModelAndView mv = new ModelAndView("redirect:/tasks");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (optional.isPresent()) {
            this.taskRepository.deleteById(id);
            mv.addObject("mensagem", "Task#" + id + "Deletada com sucesso!");
            mv.addObject("error", false);
        } else {
            mv.addObject("mensagem", "DELETE ERROR: Task #" + id + "não encontrada no banco!");
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