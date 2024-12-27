package se.berellstudios.server.controller;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.berellstudios.server.entities.UserEntity;
import se.berellstudios.server.repositories.UserRepository;
import se.berellstudios.server.services.UserService;
import se.berellstudios.server.utils.JWTUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public Map<String, String> registerUser(@RequestBody UserEntity newUser) {
        try {
            userService.registerUser(newUser);
            return Map.of("message", "User registered successfully!");
        } catch (IllegalArgumentException e) {
            return Map.of("error", "User with this email already exists!");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, Object> credentials, HttpSession session) throws JOSEException {
        String email = (String) credentials.get("email");
        String password = (String) credentials.get("password");

        boolean loginSuccessful = userService.loginUser(email, password, session);
        if (loginSuccessful) {
            // Fetch user from the database to get the role
            UserEntity user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
            }

            String role = user.getRole();
            String token = JWTUtil.generateToken(email, role); //Pass the role to the token generator

            // Return token in the response
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        //This returns a simple response when the server is up and reachable
        return Map.of("message", "Server is running and connected!");
    }
}
