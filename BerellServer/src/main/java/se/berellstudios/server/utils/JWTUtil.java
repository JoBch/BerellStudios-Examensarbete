package se.berellstudios.server.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import se.berellstudios.server.entities.UserEntity;

import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {

    public static String generateToken(String username, String role) throws JOSEException {
        final String SECRET_KEY = "RmV2dDJDZzJ5MkVma1B4R3lNdE1qYzBHRnBzYklBUTA=";
        final long TOKEN_VALIDITY = 100 * 60 * 60 * 10;

        //Create the HMAC signer with the secret key
        JWSSigner signer = new MACSigner(SECRET_KEY.getBytes());

        //Add role to the claims
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("role", role) //Include the user's role
                .issueTime(new Date(System.currentTimeMillis()))
                .expirationTime(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .build();

        //Create the JWT with the header and claims
        JWSObject jwsObject = new JWSObject(
                new JWSHeader(JWSAlgorithm.HS256),
                new Payload(claimsSet.toJSONObject())
        );

        jwsObject.sign(signer);
        return jwsObject.serialize();
    }


    //Validate and parse the JWT token
    public boolean validateToken(String token) throws JOSEException, java.text.ParseException {
        final String SECRET_KEY = "RmV2dDJDZzJ5MkVma1B4R3lNdE1qYzBHRnBzYklBUTA=";

        JWSObject jwsObject = JWSObject.parse(token);

        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
        return jwsObject.verify(verifier);
    }


    //Extract the username (subject) from the JWT token
    public String extractUsername(String token) throws java.text.ParseException {
        JWSObject jwsObject = JWSObject.parse(token);
        JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
        return claimsSet.getSubject();
    }

    //To extract the role from the token(if we need it)
    public String extractRole(String token) throws java.text.ParseException {
        JWSObject jwsObject = JWSObject.parse(token);
        JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
        return claimsSet.getClaim("role").toString();
    }

    //Check if the token has expired
    public boolean isTokenExpired(String token) throws java.text.ParseException {
        JWSObject jwsObject = JWSObject.parse(token);
        JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
        Date expiration = claimsSet.getExpirationTime();
        return expiration.before(new Date());
    }

    //Checking the token in controllers
    public ResponseEntity<Map<String, String>> jwtCheck(String token, UserEntity user, Map<String, String> response) throws Exception {

        //Check if the token is present
        if (token == null || !token.startsWith("Bearer ")) {
            response.put("message", "No token provided");
            return ResponseEntity.badRequest().body(response);
        }

        //Extract the token from the header and validate it
        String jwtToken = token.substring(7);
        if (!validateToken(jwtToken)) {
            response.put("message", "Invalid token");
            return ResponseEntity.badRequest().body(response);
        }

        //Check if the token has expired
        if (isTokenExpired(jwtToken)) {
            response.put("message", "Token has expired");
            return ResponseEntity.badRequest().body(response);
        }

        if (user == null) {
            response.put("message", "User not found");
            return ResponseEntity.badRequest().body(response);
        }
        return null;
    }
}
