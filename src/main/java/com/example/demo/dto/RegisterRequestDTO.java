package com.example.demo.dto;

import com.example.demo.models.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;


public class RegisterRequestDTO {
    @NotBlank(message = "O nome de usuário é obrigatório")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Column(unique = true)
    private String email;

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

    public User toEntity() {
        User user = new User();
        user.setName(this.username);
        user.setPassword(this.password);
        user.setEmail(this.email);
        return user;
    }

    public static RegisterRequestDTO fromEntity(User user) {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername(user.getName());
        dto.setPassword(user.getPassword());
        return dto;
    }

}