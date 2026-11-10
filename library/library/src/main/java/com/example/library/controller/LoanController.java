package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.entity.Loan;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LoanRepository;
import com.example.library.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ReaderRepository readerRepository;
    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public String listLoans(Model model) {
        model.addAttribute("loans", loanRepository.findAll());
        return "loans/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("loan", new Loan());
        model.addAttribute("readers", readerRepository.findAll());
        // Показываем только СВОБОДНЫЕ книги для выдачи
        model.addAttribute("books", bookRepository.findAll().stream()
                .filter(b -> "Свободна".equals(b.getStatus()) || b.getStatus() == null)
                .toList());
        return "loans/form";
    }

    @PostMapping("/save")
    public String saveLoan(@ModelAttribute Loan loan) {
        // 1. Сохраняем выдачу
        loanRepository.save(loan);

        // 2. Меняем статус книги на "Занята"
        Book book = loan.getBook();
        if (book != null) {
            book.setStatus("Занята");
            bookRepository.save(book);
        }

        return "redirect:/loans";
    }

    @GetMapping("/delete/{id}")
    public String deleteLoan(@PathVariable Long id) {
        // 1. Находим выдачу, чтобы узнать, какую книгу возвращают
        Loan loan = loanRepository.findById(id).orElse(null);

        if (loan != null) {
            // 2. Меняем статус книги обратно на "Свободна"
            Book book = loan.getBook();
            if (book != null) {
                book.setStatus("Свободна");
                bookRepository.save(book);
            }
            // 3. Удаляем запись о выдаче
            loanRepository.deleteById(id);
        }

        return "redirect:/loans";
    }
}