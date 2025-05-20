package com.example.demo.controllers;

import com.example.demo.dto.CreateEnterpriseDTO;
import com.example.demo.dto.RegisterRequestDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EnterpriseController {

    @GetMapping("createEnterprise")
    public ModelAndView createEnterprise() {
        ModelAndView mv = new ModelAndView("CreateEnterprise_Page/index.html");
        mv.addObject("createEnterpriseDTO", new CreateEnterpriseDTO());
        return mv;
    }


}
