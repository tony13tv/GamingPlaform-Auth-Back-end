package com.d1gaming.platform.auth.controllers;

import com.d1gaming.platform.auth.models.User;
import com.d1gaming.platform.auth.models.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @GetMapping("/login") public User login(@RequestParam String userEmail, @RequestParam String userPassword) throws ExecutionException, InterruptedException, FirebaseAuthException {
        return userService.login(userEmail, userPassword);
    }

    @PostMapping("/login") public User login(@RequestBody User user) throws ExecutionException, InterruptedException, FirebaseAuthException {
        return userService.login(user.getUserEmail(), user.getUserPassword());
    }

    @PostMapping("/signup") public User signup(@RequestBody User user) throws FirebaseAuthException {
        return userService.signup(user);
    }
}
