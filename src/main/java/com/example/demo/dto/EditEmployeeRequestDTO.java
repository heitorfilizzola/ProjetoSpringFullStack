package com.example.demo.dto;

import com.example.demo.models.RolesUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EditEmployeeRequestDTO {

    @NotBlank(message = "O ID do funcionário é obrigatório")
    private String id;

    @NotBlank(message = "O nome de usuário é obrigatório")
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    // A senha é opcional. Se em branco, não será alterada.
    // Se preenchida, deve ter no mínimo 6 caracteres.
    @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
    private String newPassword;

    @NotNull(message = "O Cargo não pode estar em branco")
    private RolesUser rolesUser;

    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public RolesUser getRolesUser() {
        return rolesUser;
    }

    public void setRolesUser(RolesUser rolesUser) {
        this.rolesUser = rolesUser;
    }
}