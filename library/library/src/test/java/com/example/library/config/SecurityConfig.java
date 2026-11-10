package com.example.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Отключаем защиту CSRF, чтобы форма добавления книги могла отправить POST-запрос
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем всем: главную, логин, статику
                        .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()

                        // Админские страницы (добавление, удаление книг, читатели, выдачи)
                        .requestMatchers("/readers/**", "/loans/**", "/books/new", "/books/save", "/books/delete/**").hasRole("ADMIN")

                        // Каталог книг доступен всем авторизованным
                        .requestMatchers("/books").hasAnyRole("ADMIN", "USER")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );
        return http.build();
    }
}