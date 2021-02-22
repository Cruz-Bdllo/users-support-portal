package com.code.supportportal.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.code.supportportal.principal.UserDetailsPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.code.supportportal.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

public class JwtTokenProvider {

    @Value("${token.secret}")
    private String secretKey;

    /* ~ For create TOKEN
    ---------------------------------------------- */
    public String createToken(UserDetailsPrincipal userPrincipal) {
        String[] claims = getClaimsForUser(userPrincipal);
        return JWT.create()
                .withIssuer(PROPERTY_TOKEN)
                .withAudience(ASDE_ADMINISTRATOR)
                .withIssuedAt(new Date())
                .withExpiresAt(java.sql.Date.valueOf(LocalDate.now().plusDays(EXPIRATION_TIME)))
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .sign(Algorithm.HMAC512(secretKey.getBytes()));
    } // end method

    public String[] getClaimsForUser(UserDetailsPrincipal userPrincipal) {
        List<String> authorities = new ArrayList<>();
        userPrincipal.getAuthorities()
                .forEach(auth -> authorities.add(auth.getAuthority()));
        return authorities.toArray(new String[0]);
    }


    /* ~ For extract TOKEN
    ---------------------------------------------- */
    public Set<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsForToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    } // end method

    private String[] getClaimsForToken(String token) {
        JWTVerifier verifier = getVerifierJWT();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getVerifierJWT() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secretKey);
            verifier = JWT.require(algorithm)
                    .withIssuer(PROPERTY_TOKEN)
                    .build();
        }catch(JWTVerificationException ex){
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return null;
    }

} // end class facade
