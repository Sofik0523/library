package com.example.library.controller;

import com.example.library.entity.Reader;
import com.example.library.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/readers")
public class ReaderController {

    @Autowired
    private ReaderRepository readerRepository;

    @GetMapping
    public String listReaders(Model model) {
        model.addAttribute("readers", readerRepository.findAll());
        return "readers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("reader", new Reader());
        return "readers/form";
    }

    @PostMapping("/save")
    public String saveReader(@ModelAttribute Reader reader) {
        readerRepository.save(reader);
        return "redirect:/readers";
    }

    @GetMapping("/delete/{id}")
    public String deleteReader(@PathVariable Long id) {
        readerRepository.deleteById(id);
        return "redirect:/readers";
    }
}