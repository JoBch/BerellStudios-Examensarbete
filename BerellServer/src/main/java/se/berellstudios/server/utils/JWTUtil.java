package se.berellstudios.server.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import exceptions.JwtExceptions;
import org.springframework.stereotype.Component;
import se.berellstudios.server.entities.UserEntity;

import java.text.ParseException;
import java.util.Date;

@Component
public class JWTUtil {

    //Accesstoken for the usual stuff
    public static String generateAccessToken(String email, String role) throws JOSEException {
        final String SECRET_KEY = "RmV2dDJDZzJ5MkVma1B4R3lNdE1qYzBHRnBzYklBUTA=";
        final long TOKEN_VALIDITY = 100 * 60 * 60 * 10; //1 hour

        //Create the HMAC signer with the secret key
        JWSSigner signer = new MACSigner(SECRET_KEY.getBytes());

        //Add role to the claims
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(email)
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

    //RefreshToken to keep the user loggedin
    public static String generateRefreshToken(String email) throws JOSEException {
        //TODO ändra key
        final String SECRET_KEY = "RmV2dDJDZzJ5MkVma1B4R3lNdE1qYzBHRnBzYklBUTA=";
        final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 30; //30 days

        JWSSigner signer = new MACSigner(SECRET_KEY.getBytes());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(email)
                .issueTime(new Date(System.currentTimeMillis()))
                .expirationTime(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .build();

        JWSObject jwsObject = new JWSObject(
                new JWSHeader(JWSAlgorithm.HS256),
                new Payload(claimsSet.toJSONObject())
        );

        jwsObject.sign(signer);
        return jwsObject.serialize();
    }

    //Validate and parse the JWT token
    public boolean validateToken(String token) {
        final String SECRET_KEY = "RmV2dDJDZzJ5MkVma1B4R3lNdE1qYzBHRnBzYklBUTA=";
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
            return jwsObject.verify(verifier);
        } catch (ParseException | JOSEException e) {
            throw new JwtExceptions.InvalidTokenException("Error parsing the token");
        }
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

    //Checking the JwtToken against several statements.
    //TODO funkar denna verkligen som planerat?
    public void jwtCheck(String token, UserEntity user) throws JwtExceptions.InvalidTokenException, JwtExceptions.ExpiredTokenException,
            JwtExceptions.UserNotFoundException, ParseException, JOSEException {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new JwtExceptions.InvalidTokenException("No token provided or invalid format");
        }

        String jwtToken = token.substring(7);

        if (!validateToken(jwtToken)) {
            throw new JwtExceptions.InvalidTokenException("Invalid token");
        }

        if (isTokenExpired(jwtToken)) {
            throw new JwtExceptions.ExpiredTokenException("Token has expired");
        }

        if (user == null) {
            throw new JwtExceptions.UserNotFoundException("User not found");
        }
    }
}