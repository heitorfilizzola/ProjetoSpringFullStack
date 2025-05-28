package com.example.demo.dto;

import com.example.demo.models.StatusTask;
import com.example.demo.models.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskRequestDTO {

    @NotBlank(message = "O título não pode estar em branco")
    private String title;

    @NotBlank(message = "O conteúdo não pode estar em branco")
    private String content;

    @NotNull(message = "A tarefa deve ter um status selecionado")
    private StatusTask statusTask;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public StatusTask getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(StatusTask statusTask) {
        this.statusTask = statusTask;
    }

    public Task toTask(){
        Task task = new Task();
        task.setTitle(this.title);
        task.setContent(this.content);
        task.setStatusTask(this.statusTask);
        return task;
    }

    public static TaskRequestDTO fromTask(Task task){
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
        taskRequestDTO.setTitle(task.getTitle());
        taskRequestDTO.setContent(task.getContent());
        taskRequestDTO.setStatusTask(task.getStatusTask());
        return taskRequestDTO;
    }
}