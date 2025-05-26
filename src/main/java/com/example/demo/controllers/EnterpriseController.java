package com.example.demo.controllers;

import com.example.demo.dto.CreateEmployeeDTO;
import com.example.demo.dto.CreateEnterpriseDTO;
import com.example.demo.dto.RegisterRequestDTO;
import com.example.demo.dto.TaskRequestDTO;
import com.example.demo.models.Enterprise;
import com.example.demo.models.User;
import com.example.demo.repositories.EnterpriseRepository;
import com.example.demo.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static com.example.demo.models.RolesUser.administrator;

@Controller
public class EnterpriseController {

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("Enterprise")
    public ModelAndView showEnterprise() {
        ModelAndView mv = new ModelAndView("Enterprise_Page/index.html");
        return mv;
    }

    @PostMapping("/CriarEmpresa")
    public ModelAndView CreateEnterprise(@Valid @ModelAttribute("criarEmpresa") CreateEnterpriseDTO createEnterpriseDTO) {
        Enterprise enterprise = createEnterpriseDTO.toEntity();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> userOptional = userRepository.findByName(username);

        User user = userOptional.get();
        enterpriseRepository.save(enterprise);

        user.setEnterprise(enterprise);
        user.setEnterpriseAdmin(enterprise.getId());
        user.setRolesUser(administrator);

        userRepository.save(user);


        return new ModelAndView("redirect:/Enterprise");
    }

    @GetMapping("CriarEmpresa")
    public ModelAndView showCreateEnterprise() {
        ModelAndView mv = new ModelAndView("CreateEnterprise_Page/index.html");
        mv.addObject("criarEmpresa", new CreateEnterpriseDTO());
        return mv;
    }


    @GetMapping("createEmployee")
    public ModelAndView createEmployee() {
        ModelAndView mv = new ModelAndView("CreateEmployee_Page/index.html");
        mv.addObject("createEmployeeDTO", new CreateEmployeeDTO());
        return mv;
    }


}
