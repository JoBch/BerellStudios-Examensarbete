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

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    //Register new user
    @PostMapping("/register")
    public Map<String, String> registerUser(@RequestBody UserEntity newUser) {
        try {
            userService.registerUser(newUser);
            return Map.of("message", "User registered successfully!");
        } catch (IllegalArgumentException e) {
            return Map.of("error", "User with this email already exists!");
        }
    }

    //Logging in against db and generating tokens
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, Object> credentials, HttpSession session) throws JOSEException {
        String email = (String) credentials.get("email");
        String password = (String) credentials.get("password");
        boolean rememberMe = (Boolean) credentials.getOrDefault("rememberMe", false); //Default to false

        boolean loginSuccessful = userService.loginUser(email, password, session);
        if (loginSuccessful) {
            UserEntity user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
            }

            String role = user.getRole();
            String accessToken = JWTUtil.generateAccessToken(email, role); //Short-lived access token
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);

            //Generate long-lived refresh token if the bool is true from app
            if (rememberMe) {
                String refreshToken = JWTUtil.generateRefreshToken(email);
                response.put("refreshToken", refreshToken);
            }

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }
    }


    //Giving the user a new accesstoken looking at the refreshtoken
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) throws JOSEException, ParseException {
        String refreshToken = request.get("refreshToken");

        if (jwtUtil.validateToken(refreshToken)) {
            String email = jwtUtil.extractUsername(refreshToken);
            //Generate new access token
            String newAccessToken = JWTUtil.generateAccessToken(email, "userRole");
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }
    }

    //This returns a simple response when the server is up and reachable
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("message", "Server is running and connected!");
    }
}
