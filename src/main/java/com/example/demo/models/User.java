package com.example.demo.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Task> tasks = new ArrayList<>();

    private RolesUser rolesUser;
    public User() {}


    public User(String id, String name, String email, String password, List<Task> tasks) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.tasks = tasks;
    }


    public User(String id, String name, String email, String password, List<Task> tasks, RolesUser rolesUser) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.tasks = tasks;
        this.rolesUser = rolesUser;
    }


    public RolesUser getRolesUser() {
        return rolesUser;
    }

    public void setRolesUser(RolesUser rolesUser) {
        this.rolesUser = rolesUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setResetToken(String resetToken) {

    }
}
