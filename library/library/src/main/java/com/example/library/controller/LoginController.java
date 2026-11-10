package com.example.library.controller;

import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginProcess(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {

        if (username.equals("admin") && password.equals("admin")) {
            return "redirect:/home?role=admin";
        }
        else if (username.equals("user") && password.equals("user")) {
            return "redirect:/home?role=user";
        }
        else {
            model.addAttribute("error", "Неверный логин или пароль");
            return "login";
        }
    }
}