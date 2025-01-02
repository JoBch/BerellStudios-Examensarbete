package se.berellstudios.server.controller;

import com.nimbusds.jose.JOSEException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.berellstudios.server.utils.JWTUtil;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/jwt")
public class JWTController {

    private final JWTUtil jwtUtil = new JWTUtil();

    //Giving the user a new accesstoken looking at the refreshtoken
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) throws JOSEException, ParseException {
        String refreshToken = request.get("refreshToken");

        if (jwtUtil.validateToken(refreshToken)) {
            String email = jwtUtil.extractEmail(refreshToken);
            //Generate new access token
            String newAccessToken = JWTUtil.generateAccessToken(email, "userRole", "username");
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }
    }

}
