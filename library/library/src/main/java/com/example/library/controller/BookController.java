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

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ReaderRepository readerRepository;

    @GetMapping
    public String listBooks(@RequestParam(required = false, defaultValue = "user") String role, Model model) {
        List<Book> books = bookRepository.findAll();

        // Пересчитываем прогресс для занятых книг на основе дат выдачи и возврата
        LocalDate today = LocalDate.now();
        for (Book book : books) {
            if ("Занята".equals(book.getStatus())) {
                // Находим активную выдачу для этой книги
                Loan activeLoan = loanRepository.findAll().stream()
                        .filter(l -> l.getBook().getId().equals(book.getId()))
                        .findFirst().orElse(null);

                if (activeLoan != null && activeLoan.getIssueDate() != null && activeLoan.getDueDate() != null) {
                    try {
                        LocalDate issue = LocalDate.parse(activeLoan.getIssueDate());
                        LocalDate due = LocalDate.parse(activeLoan.getDueDate());

                        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(issue, due);
                        long daysPassed = java.time.temporal.ChronoUnit.DAYS.between(issue, today);

                        if (totalDays > 0) {
                            int percent = (int) ((daysPassed * 100) / totalDays);
                            percent = Math.max(0, Math.min(100, percent)); // Ограничиваем 0-100
                            book.setProgress(percent);
                        } else {
                            book.setProgress(100);
                        }
                    } catch (Exception e) {
                        book.setProgress(0);
                    }
                }
            } else {
                book.setProgress(0); // Если книга свободна, прогресс 0
            }
        }

        model.addAttribute("books", books);

        if ("admin".equals(role)) {
            return "books/list_admin";
        } else {
            return "books/list_user";
        }
    }

    // Выдача книги (Админ)
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        return "books/form";
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute Book book) {
        if (book.getStatus() == null || book.getStatus().isEmpty()) {
            book.setStatus("Свободна");
        }
        bookRepository.save(book);
        return "redirect:/books?role=admin";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "redirect:/books?role=admin";
    }

    // Функция для Юзера: "Хочу эту книгу"
    @GetMapping("/want/{id}")
    public String wantBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null && !"Свободна".equals(book.getStatus())) {
            // В реальности здесь была бы запись в лист ожидания.
        }
        return "redirect:/books?role=user&wanted=true";
    }

    // Функция для Юзера: Поставить оценку (теперь GET)
    @GetMapping("/rate/{id}")
    public String rateBook(@PathVariable Long id, @RequestParam Double rating) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            book.setRating(rating);
            bookRepository.save(book);
        }
        return "redirect:/books?role=user";
    }
}