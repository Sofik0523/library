package com.example.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/home")
    public String home(@RequestParam(required = false, defaultValue = "user") String role, Model model) {
        // Передаем роль на страницу
        model.addAttribute("role", role);
        return "index";
    }
}