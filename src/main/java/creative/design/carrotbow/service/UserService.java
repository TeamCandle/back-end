package creative.design.carrotbow.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.repository.UserRepository;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import creative.design.carrotbow.security.jwt.JwtUtils;
import creative.design.carrotbow.security.jwt.RefreshToken;
import creative.design.carrotbow.security.jwt.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public String refreshAccessToken(String refreshToken){
        RefreshToken token = tokenRepository.findByToken(refreshToken).orElseThrow(() -> new JWTVerificationException("invalid or expired token"));
        String username = jwtUtils.getUsernameFromToken(refreshToken, JwtUtils.REFRESH);

        if(username==null){
            tokenRepository.delete(token);
            throw new JWTVerificationException("expired token");
        }

        return jwtUtils.generateAccessToken(username);
    }
}
