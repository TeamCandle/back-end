package creative.design.carrotbow.security.jwt;



import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.access}")
    private long accessTokenExpiration;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpiration;


    public String generateAccessToken(String username) {
        return createToken(username, ACCESS, accessTokenExpiration);
    }

    public String generateRefreshToken(String username) {
        return createToken(username, REFRESH, refreshTokenExpiration);
    }

    private String createToken(String username, String type, long expiration) {
        try {
            return JWT.create()
                    .withSubject(type + "token")
                    .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                    .withClaim("username", username)
                    .sign(Algorithm.HMAC512(type + jwtSecret));
        } catch (JWTCreationException e) {
            // 토큰 생성 실패 시 예외 처리
            e.printStackTrace();
            return null;
        }
    }


    private DecodedJWT verifyToken(String token, String type) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(type + jwtSecret)).build();
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            // 토큰 검증 실패 시 예외 처리
            e.printStackTrace();
            return null;
        }
    }


    public String getUsernameFromToken(String token, String type) {
        DecodedJWT decodedJWT = verifyToken(token, type);
        if (decodedJWT == null) {
            // 토큰 검증 실패 시 처리
            return null;
        }
        return decodedJWT.getClaim("username").asString();
    }
}
