package com.example.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "books")
public class Book extends Publication {
    private int pages;
    private String status = "Свободна";
    private Double rating = 0.0;
    private int progress = 0; // Процент прочтения (0-100)
}