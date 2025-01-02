package se.berellstudios.server.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//Hashing passwords using BCrypt library from Spring-Security
public class Hashing {
    private final BCryptPasswordEncoder passwordEncoder;

    public Hashing() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}