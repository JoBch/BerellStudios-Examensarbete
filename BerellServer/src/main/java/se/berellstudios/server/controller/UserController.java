package se.berellstudios.server.controller;

import org.springframework.web.bind.annotation.*;
import se.berellstudios.server.entities.UserEntity;
import se.berellstudios.server.services.UserService;
import se.berellstudios.server.utils.JWTUtil;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

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
    public Map<String, String> loginUser(@RequestBody Map<String, Object> credentials, HttpSession session) throws JOSEException {
        String email = (String) credentials.get("email");
        String password = (String) credentials.get("password");

        boolean loginSuccessful = userService.loginUser(email, password, session);
        if (loginSuccessful) {

            String token = JWTUtil.generateToken(email);

            //Return token in response
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response).getBody();
        } else {
            return Map.of("error", "Invalid email or password");
        }
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        // This returns a simple response when the server is up and reachable
        return Map.of("message", "Server is running and connected!");
    }
}
