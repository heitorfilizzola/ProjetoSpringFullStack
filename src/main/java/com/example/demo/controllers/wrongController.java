package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class wrongController {
    @GetMapping("wrong")
    public ModelAndView wrong() {
        ModelAndView mv = new ModelAndView("wrong");
        return mv;
    }

    @GetMapping("notfound")
    public ModelAndView notfound() {
        ModelAndView mv = new ModelAndView("notfound");
        return mv;
    }

}


// PARA DEBUG