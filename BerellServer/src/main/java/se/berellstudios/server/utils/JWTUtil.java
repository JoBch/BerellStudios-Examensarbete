package se.berellstudios.server.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    //Generate JWT token
    public static String generateToken(String username) throws JOSEException {
        final String SECRET_KEY = "RmV2dDJDZzJ5MkVma1B4R3lNdE1qYzBHRnBzYklBUTA=";
        final long TOKEN_VALIDITY = 100 * 60 * 60 * 10;

        //Create the HMAC signer with the secret key
        JWSSigner signer = new MACSigner(SECRET_KEY.getBytes());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
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

    //Check if the token has expired
    public boolean isTokenExpired(String token) throws java.text.ParseException {
        JWSObject jwsObject = JWSObject.parse(token);
        JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
        Date expiration = claimsSet.getExpirationTime();
        return expiration.before(new Date());
    }
}
