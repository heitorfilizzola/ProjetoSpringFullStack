package com.example.demo.dto;

import com.example.demo.models.StatusTask;
import com.example.demo.models.Task;
import jakarta.validation.constraints.NotNull;

public class TaskEditRequestDTO {
    @NotNull(message = "O id não pode estar em branco")
    private Long idTask;
    @NotNull(message = "O título não pode estar em branco")
    private String title;
    @NotNull(message = "O conteúdo não pode estar em branco")
    private String content;
    @NotNull(message = "A tarefa não pode ter um status nulo")
    private StatusTask statusTask;

    public Long getIdTask() {return idTask;}

    public void setIdTask(Long idTask) { this.idTask = idTask;}

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

    public static TaskEditRequestDTO fromTask(Task task) {
        TaskEditRequestDTO taskEditRequestDTO = new TaskEditRequestDTO();
        taskEditRequestDTO.setIdTask(task.getIdTask());  // Certifique-se de incluir o ID
        taskEditRequestDTO.setTitle(task.getTitle());
        taskEditRequestDTO.setContent(task.getContent());
        taskEditRequestDTO.setStatusTask(task.getStatusTask());
        return taskEditRequestDTO;
    }

}
