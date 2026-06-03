package com.history.controller;

import com.history.model.User;
import com.history.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password) {

        // Проверка через репозиторий
        if (userRepository.existsByUsername(username)) {
            return "Данное имя уже занято!";
        }

        User user = new User(username, password);

        if (password.length() < 8) {
            return "Пароль должен содержать не менее 8 символов";
        }

        try {
            userRepository.save(user);
            return "OK";
        } catch (DataIntegrityViolationException e) {
            return "Ошибка: пользователь уже существует";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            return "Пользователь не найден";
        }

        if (!user.getPassword().equals(password)) {
            return "Неверный пароль";
        }

        return "Успешный вход";
    }
}