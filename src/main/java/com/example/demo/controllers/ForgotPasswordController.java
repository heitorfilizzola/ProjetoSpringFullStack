package com.example.demo.controllers;

import com.example.demo.dto.ForgotPasswordRequestDTO;
import com.example.demo.dto.ResetPasswordDTO;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.EmailService; // Importar EmailService
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired // Injetar o EmailService
    private EmailService emailService;

    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 60;

    @GetMapping("/forgotpassword")
    public ModelAndView showForgotPasswordPage() {
        ModelAndView mv = new ModelAndView("ForgotPassword_Page/index");
        mv.addObject("forgotPasswordRequestDTO", new ForgotPasswordRequestDTO());
        return mv;
    }

    @PostMapping("/forgotpassword")
    public ModelAndView processForgotPassword(
            @ModelAttribute("forgotPasswordRequestDTO") @Valid ForgotPasswordRequestDTO forgotPasswordRequestDTO,
            BindingResult bindingResult,
            HttpServletRequest request,
            ModelAndView mv,
            RedirectAttributes redirectAttributes) { // Adicionar RedirectAttributes aqui também

        mv.setViewName("ForgotPassword_Page/index");

        if (bindingResult.hasErrors()) {
            return mv;
        }

        Optional<User> userOptional = userRepository.findByEmail(forgotPasswordRequestDTO.getEmail());

        if (userOptional.isEmpty()) {
            mv.addObject("error", "E-mail não encontrado!");
            return mv;
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(EXPIRE_TOKEN_AFTER_MINUTES));
        userRepository.save(user);

        // Construir a URL base para o link de reset
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath();

        // Enviar o e-mail
        try {
            emailService.sendPasswordResetEmail(
                    user.getEmail(),
                    "Redefinição de Senha - NoteSync", // Assunto do e-mail
                    token,
                    baseUrl
            );
            // Adicionar mensagem de sucesso para ser exibida após o redirect
            redirectAttributes.addFlashAttribute("message", "Um link de recuperação foi enviado para o seu e-mail.");
        } catch (Exception e) {
            // Se o envio de e-mail falhar, informe o usuário.
            // Em produção, você pode logar o erro e tentar novamente, ou ter um fluxo alternativo.
            System.err.println("Falha ao enviar e-mail no controller: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Não foi possível enviar o e-mail de recuperação. Tente novamente mais tarde.");
        }

        // Redirecionar para a página de forgotpassword para exibir a mensagem (ou para login)
        mv.setViewName("redirect:/forgotpassword");
        return mv;
    }

    // Os métodos showResetPasswordPage e handleResetPassword permanecem os mesmos
    // que você já implementou no passo anterior.

    @GetMapping("/reset-password")
    public ModelAndView showResetPasswordPage(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        ModelAndView mv = new ModelAndView();
        Optional<User> userOptional = userRepository.findByResetPasswordToken(token);

        if (userOptional.isEmpty() || userOptional.get().getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Token de redefinição inválido ou expirado. Por favor, solicite um novo.");
            mv.setViewName("redirect:/forgotpassword");
            return mv;
        }

        ResetPasswordDTO resetDto = new ResetPasswordDTO();
        resetDto.setToken(token);

        mv.setViewName("ResetPassword_Page/index");
        mv.addObject("resetPasswordDTO", resetDto);
        return mv;
    }

    @PostMapping("/reset-password")
    public ModelAndView handleResetPassword(
            @ModelAttribute("resetPasswordDTO") @Valid ResetPasswordDTO resetPasswordDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        ModelAndView mv = new ModelAndView();

        if (bindingResult.hasErrors()) {
            mv.setViewName("ResetPassword_Page/index");
            return mv;
        }

        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "As senhas não coincidem.");
            mv.setViewName("ResetPassword_Page/index");
            return mv;
        }

        Optional<User> userOptional = userRepository.findByResetPasswordToken(resetPasswordDTO.getToken());

        if (userOptional.isEmpty() || userOptional.get().getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Token inválido ou expirado. Por favor, solicite um novo.");
            mv.setViewName("redirect:/forgotpassword");
            return mv;
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Sua senha foi redefinida com sucesso! Você já pode fazer login.");
        mv.setViewName("redirect:/login");
        return mv;
    }
}