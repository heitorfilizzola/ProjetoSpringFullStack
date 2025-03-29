package com.example.demo.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ForgotPasswordRequestDTO {
    @Email(message = "O formato do email é invalido")
    @NotBlank(message = "O e-mail é obrigatório")
    @NotNull
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
