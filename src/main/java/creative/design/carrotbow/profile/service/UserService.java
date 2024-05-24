package creative.design.carrotbow.profile.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import creative.design.carrotbow.profile.domain.User;
import creative.design.carrotbow.profile.repository.UserRepository;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import creative.design.carrotbow.security.jwt.JwtUtils;
import creative.design.carrotbow.security.jwt.RefreshToken;
import creative.design.carrotbow.security.jwt.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private final JwtUtils jwtUtils;


    @Transactional(readOnly = true)
    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }

    public User find(Long id){
        return userRepository.find(id).orElse(null);
    }

    public void register(User user){
        userRepository.save(user);
    }

    public void logout(AuthenticationUser user){
        tokenRepository.deleteAll(user.getId());
    }

    public void saveRefreshToken(String refreshToken, AuthenticationUser user){
        tokenRepository.save(new RefreshToken(refreshToken, new User(user.getId())));
    }

    public HashMap<String, String> refreshAccessToken(String refreshToken, AuthenticationUser user){
        RefreshToken token = tokenRepository.findByToken(refreshToken).orElseThrow(() -> new JWTVerificationException("invalid or expired token"));
        String username = jwtUtils.getUsernameFromToken(refreshToken, JwtUtils.REFRESH);

        if(username==null){
            tokenRepository.delete(token);
            throw new JWTVerificationException("expired token");
        }

        HashMap<String, String> keys= new HashMap<>();

        String newAccessToken = jwtUtils.generateAccessToken(username);
        String newRefreshToken = jwtUtils.generateRefreshToken(username);

        keys.put("accessToken", newAccessToken);
        keys.put("refreshToken", newRefreshToken);

        tokenRepository.delete(token);
        tokenRepository.save(new RefreshToken(newRefreshToken, new User(user.getId())));


        return keys;
    }
}
