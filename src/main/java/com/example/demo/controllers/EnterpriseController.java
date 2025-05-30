package com.example.demo.controllers;

import com.example.demo.dto.CreateEmployeeDTO;
import com.example.demo.dto.CreateEnterpriseDTO;
import com.example.demo.models.Enterprise;
import com.example.demo.models.RolesUser;
import com.example.demo.models.User;
import com.example.demo.repositories.EnterpriseRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static com.example.demo.models.RolesUser.administrator;

@Controller
@RequestMapping("/minha-empresa")
public class EnterpriseController {

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Optional<User> getAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByName(username);
    }

    @GetMapping
    public ModelAndView showMyEnterprisePage(RedirectAttributes redirectAttributes, Authentication authentication) {
        Optional<User> userOptional = getAuthenticatedUser(authentication);
        if (userOptional.isEmpty()) return new ModelAndView("redirect:/login");

        User currentUser = userOptional.get();
        ModelAndView mv = new ModelAndView("CreateEnterprise_Page/index.html");
        mv.addObject("pageTitle", "Minha Empresa");

        redirectAttributes.getFlashAttributes().forEach(mv::addObject);

        Enterprise enterprise = currentUser.getEnterprise();
        if (enterprise != null) {
            mv.addObject("currentUserEnterprise", enterprise);
            mv.addObject("employeeList", userRepository.findByEnterpriseId(enterprise.getId()));
            mv.addObject("adminUserId", currentUser.getId());
            if (!mv.getModel().containsKey("createEmployeeDTO")) {
                mv.addObject("createEmployeeDTO", new CreateEmployeeDTO());
            }
            mv.addObject("allRoles", RolesUser.values());
        } else {
            mv.addObject("pageTitle", "Criar Empresa");
            if (!mv.getModel().containsKey("criarEmpresa")) {
                mv.addObject("criarEmpresa", new CreateEnterpriseDTO());
            }
        }
        return mv;
    }

    @PostMapping("/criar")
    @Transactional
    public ModelAndView createEnterprise(@Valid @ModelAttribute("criarEmpresa") CreateEnterpriseDTO createEnterpriseDTO,
                                         BindingResult bindingResult,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = getAuthenticatedUser(authentication);
        if (userOptional.isEmpty()) return new ModelAndView("redirect:/login");

        User user = userOptional.get();
        if (user.getEnterprise() != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você já possui uma empresa.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.criarEmpresa", bindingResult);
            redirectAttributes.addFlashAttribute("criarEmpresa", createEnterpriseDTO);
            return new ModelAndView("redirect:/minha-empresa");
        }

        if (enterpriseRepository.findByName(createEnterpriseDTO.getEnterpriseName()).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nome de empresa já em uso.");
            redirectAttributes.addFlashAttribute("criarEmpresaComErro", createEnterpriseDTO);
            return new ModelAndView("redirect:/minha-empresa");
        }

        Enterprise enterprise = createEnterpriseDTO.toEntity();
        enterpriseRepository.save(enterprise);

        user.setEnterprise(enterprise);
        user.setEnterpriseAdmin(enterprise.getId());
        user.setRolesUser(administrator);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "Empresa criada com sucesso!");
        return new ModelAndView("redirect:/minha-empresa");
    }

    @PostMapping("/deletar")
    @Transactional
    public ModelAndView deleteEnterprise(Authentication authentication, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = getAuthenticatedUser(authentication);
        if (userOptional.isEmpty()) return new ModelAndView("redirect:/login");

        User currentUser = userOptional.get();
        Enterprise enterprise = currentUser.getEnterprise();

        if (enterprise == null || currentUser.getEnterpriseAdmin() == null || !currentUser.getEnterpriseAdmin().equals(enterprise.getId())) {
            return new ModelAndView("redirect:/minha-empresa");
        }

        List<User> users = userRepository.findByEnterpriseId(enterprise.getId());
        users.forEach(u -> {
            u.setEnterprise(null);
            u.setEnterpriseAdmin(null);
            userRepository.save(u);
        });

        enterpriseRepository.delete(enterprise);
        redirectAttributes.addFlashAttribute("successMessage", "Empresa excluída com sucesso.");
        return new ModelAndView("redirect:/minha-empresa");
    }

    @GetMapping("/adicionar-funcionario")
    public ModelAndView showAddEmployeeForm(Authentication authentication, RedirectAttributes redirectAttributes) {
        Optional<User> adminOptional = getAuthenticatedUser(authentication);
        if (adminOptional.isEmpty() || adminOptional.get().getEnterprise() == null || adminOptional.get().getEnterpriseAdmin() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Permissão negada.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        ModelAndView mv = new ModelAndView("CreateEmployee_Page/index.html");
        mv.addObject("createEmployeeDTO", new CreateEmployeeDTO());
        mv.addObject("enterpriseName", adminOptional.get().getEnterprise().getName());
        mv.addObject("formAction", "/minha-empresa/adicionar-funcionario");
        mv.addObject("pageTitle", "Adicionar Funcionário");
        mv.addObject("allRoles", RolesUser.values());
        return mv;
    }

    @PostMapping("/adicionar-funcionario")
    @Transactional
    public ModelAndView addEmployeeToEnterprise(@Valid @ModelAttribute("createEmployeeDTO") CreateEmployeeDTO createEmployeeDTO,
                                                BindingResult bindingResult,
                                                Authentication authentication,
                                                RedirectAttributes redirectAttributes) {
        Optional<User> adminOptional = getAuthenticatedUser(authentication);
        if (adminOptional.isEmpty() || adminOptional.get().getEnterprise() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Operação não permitida.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        User admin = adminOptional.get();
        Enterprise enterprise = admin.getEnterprise();

        if (userRepository.findByName(createEmployeeDTO.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.username", "Nome de usuário já em uso.");
        }
        if (userRepository.findByEmail(createEmployeeDTO.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.email", "E-mail já em uso.");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.createEmployeeDTO", bindingResult);
            redirectAttributes.addFlashAttribute("createEmployeeDTOWithErrors", createEmployeeDTO);
            return new ModelAndView("redirect:/minha-empresa/adicionar-funcionario");
        }

        User newEmployee = createEmployeeDTO.toEntity();
        newEmployee.setEnterprise(enterprise);
        newEmployee.setPassword(passwordEncoder.encode(createEmployeeDTO.getPassword()));
        if (newEmployee.getRolesUser() == null) {
            newEmployee.setRolesUser(RolesUser.user);
        }

        userRepository.save(newEmployee);
        redirectAttributes.addFlashAttribute("successMessage", "Funcionário adicionado com sucesso!");
        return new ModelAndView("redirect:/minha-empresa");
    }

    @PostMapping("/remover-funcionario/{employeeId}")
    @Transactional
    public ModelAndView removeEmployeeFromEnterprise(@PathVariable String employeeId,
                                                     Authentication authentication,
                                                     RedirectAttributes redirectAttributes) {
        Optional<User> adminOptional = getAuthenticatedUser(authentication);
        if (adminOptional.isEmpty() || adminOptional.get().getEnterprise() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Operação não permitida.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        User admin = adminOptional.get();
        Enterprise enterprise = admin.getEnterprise();

        if (employeeId.equals(admin.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não pode se remover como administrador.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        Optional<User> employeeOptional = userRepository.findById(employeeId);
        if (employeeOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Funcionário não encontrado.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        User employee = employeeOptional.get();
        if (employee.getEnterprise() == null || !employee.getEnterprise().getId().equals(enterprise.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Funcionário não pertence à sua empresa.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        employee.setEnterprise(null);
        userRepository.save(employee);

        redirectAttributes.addFlashAttribute("successMessage", "Funcionário removido com sucesso.");
        return new ModelAndView("redirect:/minha-empresa");
    }
}