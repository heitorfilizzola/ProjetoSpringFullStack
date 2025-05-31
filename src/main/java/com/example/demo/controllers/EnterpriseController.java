package com.example.demo.controllers;

import com.example.demo.dto.CreateEmployeeDTO;
import com.example.demo.dto.CreateEnterpriseDTO;
import com.example.demo.dto.EditEmployeeRequestDTO;
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

        List<User> users = (List<User>) userRepository.findByEnterpriseId(enterprise.getId());
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
        if (adminOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não autenticado.");
            return new ModelAndView("redirect:/login");
        }

        User admin = adminOptional.get();
        Enterprise enterprise = admin.getEnterprise();

        if (enterprise == null || admin.getEnterpriseAdmin() == null || !admin.getEnterpriseAdmin().equals(enterprise.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não tem permissão para remover funcionários.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        if (employeeId.equals(admin.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não pode se remover como administrador da empresa por esta ação.");
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

         if (employee.getRolesUser() == RolesUser.administrator) {
             employee.setEnterpriseAdmin(null);
         }
        employee.setEnterpriseAdmin(null);


        userRepository.save(employee);

        redirectAttributes.addFlashAttribute("successMessage", "Funcionário removido com sucesso.");
        return new ModelAndView("redirect:/minha-empresa");
    }

    @GetMapping("/editar-funcionario/{employeeId}")
    public ModelAndView showEditEmployeeForm(@PathVariable String employeeId,
                                             Authentication authentication,
                                             RedirectAttributes redirectAttributes) {
        Optional<User> adminOptional = getAuthenticatedUser(authentication);
        if (adminOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não autenticado.");
            return new ModelAndView("redirect:/login");
        }
        User admin = adminOptional.get();
        Enterprise enterprise = admin.getEnterprise();


        if (enterprise == null || admin.getEnterpriseAdmin() == null || !admin.getEnterpriseAdmin().equals(enterprise.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não tem permissão para editar funcionários.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        Optional<User> employeeOptional = userRepository.findById(employeeId);
        if (employeeOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Funcionário não encontrado.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        User employee = employeeOptional.get();

        if (employee.getEnterprise() == null || !employee.getEnterprise().getId().equals(enterprise.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Este funcionário não pertence à sua empresa.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        // Não permitir que o admin edite a si mesmo de forma a perder o status de admin da empresa atual por esta tela
        if (employee.getId().equals(admin.getId()) && employee.getRolesUser() == RolesUser.administrator && admin.getEnterpriseAdmin().equals(enterprise.getId())) {
        }


        EditEmployeeRequestDTO editEmployeeDTO = new EditEmployeeRequestDTO();
        editEmployeeDTO.setId(employee.getId());
        editEmployeeDTO.setUsername(employee.getName());
        editEmployeeDTO.setEmail(employee.getEmail());
        editEmployeeDTO.setRolesUser(employee.getRolesUser());
        // Não populamos newPassword, pois é para alteração

        ModelAndView mv = new ModelAndView("EditEmployee_Page/index"); // Novo HTML que será criado
        mv.addObject("editEmployeeDTO", editEmployeeDTO);
        mv.addObject("employeeName", employee.getName()); // Para exibir o nome do funcionário na página
        mv.addObject("allRoles", RolesUser.values());
        mv.addObject("pageTitle", "Editar Funcionário");
        return mv;
    }

    @PostMapping("/editar-funcionario")
    @Transactional
    public ModelAndView updateEmployee(@Valid @ModelAttribute("editEmployeeDTO") EditEmployeeRequestDTO editEmployeeRequestDTO,
                                       BindingResult bindingResult,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {

        Optional<User> adminOptional = getAuthenticatedUser(authentication);
        if (adminOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não autenticado.");
            return new ModelAndView("redirect:/login");
        }

        User admin = adminOptional.get();
        Enterprise currentEnterprise = admin.getEnterprise();

        if (currentEnterprise == null || admin.getEnterpriseAdmin() == null || !admin.getEnterpriseAdmin().equals(currentEnterprise.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Você não tem permissão para atualizar informações de funcionários.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        Optional<User> employeeToUpdateOptional = userRepository.findById(editEmployeeRequestDTO.getId());
        if (employeeToUpdateOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Funcionário não encontrado para atualização.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        User employeeToUpdate = employeeToUpdateOptional.get();

        // Verificar se o funcionário a ser atualizado pertence à empresa do admin
        if (employeeToUpdate.getEnterprise() == null || !employeeToUpdate.getEnterprise().getId().equals(currentEnterprise.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Este funcionário não pertence à sua empresa.");
            return new ModelAndView("redirect:/minha-empresa");
        }

        // Validações de username e email (para garantir unicidade, exceto para o próprio usuário)
        userRepository.findFirstByName(editEmployeeRequestDTO.getUsername()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(employeeToUpdate.getId())) {
                bindingResult.rejectValue("username", "error.username", "Este nome de usuário já está em uso por outro funcionário.");
            }
        });

        userRepository.findFirstByEmail(editEmployeeRequestDTO.getEmail()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(employeeToUpdate.getId())) {
                bindingResult.rejectValue("email", "error.email", "Este e-mail já está em uso por outro funcionário.");
            }
        });

        // Validação especial: Impedir que o admin se rebaixe se for o único admin da empresa
        if (employeeToUpdate.getId().equals(admin.getId()) && // Se está editando a si mesmo
                admin.getEnterpriseAdmin() != null && admin.getEnterpriseAdmin().equals(currentEnterprise.getId()) && // E é o admin da empresa
                admin.getRolesUser() == RolesUser.administrator && // E seu cargo atual é admin
                editEmployeeRequestDTO.getRolesUser() == RolesUser.user) { // E está tentando mudar para user

            // Contar quantos administradores existem na empresa
            long adminCount = userRepository.findByEnterpriseId(currentEnterprise.getId()).stream()
                    .filter(u -> u.getRolesUser() == RolesUser.administrator && u.getEnterpriseAdmin() != null && u.getEnterpriseAdmin().equals(currentEnterprise.getId()))
                    .count();
            if (adminCount <= 1) {
                bindingResult.rejectValue("rolesUser", "error.rolesUser", "Você não pode remover seu próprio status de administrador pois é o único na empresa.");
            }
        }


        if (bindingResult.hasErrors()) {
            // Repopular informações necessárias para o formulário em caso de erro
            ModelAndView mv = new ModelAndView("EditEmployee_Page/index");
            mv.addObject("editEmployeeDTO", editEmployeeRequestDTO); // Manter os dados que o usuário tentou submeter
            mv.addObject("employeeName", employeeToUpdate.getName()); // Nome original para o título
            mv.addObject("allRoles", RolesUser.values());
            mv.addObject("pageTitle", "Editar Funcionário");
            // Adicionar os erros ao RedirectAttributes se for redirecionar, ou diretamente ao Model/ModelAndView se for renderizar a mesma view
            bindingResult.getFieldErrors().forEach(error -> {
                redirectAttributes.addFlashAttribute(error.getField() + "_error", error.getDefaultMessage());
            });
            // Ou para erros globais:
            // bindingResult.getGlobalErrors().forEach(error -> ... );

            // Para exibir erros diretamente na página sem redirect:
            // mv.addObject("bindingResult", bindingResult); // Thymeleaf pode acessar os erros diretamente
            return mv; // Renderiza a página de edição novamente com os erros
        }

        // Atualizar os campos
        employeeToUpdate.setName(editEmployeeRequestDTO.getUsername());
        employeeToUpdate.setEmail(editEmployeeRequestDTO.getEmail());

        // Atualizar senha apenas se uma nova foi fornecida
        if (editEmployeeRequestDTO.getNewPassword() != null && !editEmployeeRequestDTO.getNewPassword().isEmpty()) {
            employeeToUpdate.setPassword(passwordEncoder.encode(editEmployeeRequestDTO.getNewPassword()));
        }

        // Atualizar o cargo (role)
        employeeToUpdate.setRolesUser(editEmployeeRequestDTO.getRolesUser());

        // Se o usuário editado era o admin da empresa e foi rebaixado para 'user',
        // o campo enterpriseAdmin dele deve ser nulificado.
        // A lógica de impedir o único admin de se rebaixar já foi tratada.
        // Se ele foi promovido a admin, ou já era admin e continuou admin, o enterpriseAdmin dele deve ser o ID da empresa.
        if (editEmployeeRequestDTO.getRolesUser() == RolesUser.administrator) {
            employeeToUpdate.setEnterpriseAdmin(currentEnterprise.getId());
        } else {
            // Se o usuário que está sendo editado é o mesmo que o admin logado
            // E se o papel dele está sendo mudado de administrador para usuário
            // E ele era o administrador desta empresa
            if (employeeToUpdate.getId().equals(admin.getId()) &&
                    admin.getEnterpriseAdmin() != null &&
                    admin.getEnterpriseAdmin().equals(currentEnterprise.getId()) &&
                    admin.getRolesUser() == RolesUser.administrator && // papel original era admin
                    editEmployeeRequestDTO.getRolesUser() == RolesUser.user) { // novo papel é user
                // Esta situação foi bloqueada pela validação de único admin.
                // Se não fosse bloqueada, aqui seria o local para definir o que aconteceria.
                // Por exemplo, poderia impedir a ação ou transferir a administração.
                // No estado atual, a validação acima já impede o rebaixamento do último admin.
            } else if (employeeToUpdate.getEnterpriseAdmin() != null && employeeToUpdate.getEnterpriseAdmin().equals(currentEnterprise.getId())) {
                // Se ele era admin desta empresa e está sendo rebaixado, remove o status de admin da empresa.
                employeeToUpdate.setEnterpriseAdmin(null);
            }
        }


        userRepository.save(employeeToUpdate);

        redirectAttributes.addFlashAttribute("successMessage", "Funcionário '" + employeeToUpdate.getName() + "' atualizado com sucesso!");
        return new ModelAndView("redirect:/minha-empresa");
    }
}