package com.example.demo.dto;

import com.example.demo.models.RolesUser;
import com.example.demo.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateEmployeeDTO {
    @NotBlank(message = "O nome de usuário é obrigatório")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotNull(message = "O Cargo não pode estar em branco")
    private RolesUser rolesUser;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RolesUser getRolesUser() {
        return rolesUser;
    }

    public void setRolesUser(RolesUser rolesUser) {
        this.rolesUser = rolesUser;
    }

    public User toEntity() {
        User user = new User();
        user.setName(this.username);
        user.setEmail(this.email);
        user.setRolesUser(this.rolesUser);
        return user;
    }

    public static CreateEmployeeDTO fromEntity(User user) {
        CreateEmployeeDTO dto = new CreateEmployeeDTO();
        dto.setUsername(user.getName());
        dto.setRolesUser(user.getRolesUser());
        dto.setEmail(user.getEmail());
        return dto;
    }
}