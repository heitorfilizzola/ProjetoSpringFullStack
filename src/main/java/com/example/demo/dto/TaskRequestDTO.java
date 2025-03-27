package com.example.demo.dto;

import com.example.demo.models.Task;
import jakarta.validation.constraints.NotNull;

public class TaskRequestDTO {
    @NotNull(message = "O título não pode estar em branco")
    private String title;
    @NotNull(message = "O conteúdo não pode estar em branco")
    private String content;

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

    public Task toTask(){
        Task task = new Task();
        task.setTitle(this.title);
        task.setContent(this.content);
        return task;
    }

    public static TaskRequestDTO fromTask(Task task){
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
        taskRequestDTO.setTitle(task.getTitle());
        taskRequestDTO.setContent(task.getContent());
        return taskRequestDTO;
    }

}
