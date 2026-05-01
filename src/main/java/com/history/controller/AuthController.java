package com.history.controller;

import com.history.model.User;
import com.history.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password) {

        User existingUser = userRepository.findByUsername(username);

        if (existingUser != null) {
            return "Данное имя уже занято!"; // специальный ответ
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        userRepository.save(user);

        return "OK";
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