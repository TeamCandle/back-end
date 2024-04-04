package creative.design.carrotbow.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.repository.UserRepository;
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
    public User findReadUser(String username){
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findUser(Long id){

        return userRepository.findById(id).orElse(null);
    }

    public void registerUser(User user){
        userRepository.save(user);
    }

    public void logOut(String username){
        tokenRepository.deleteAllByUsername(username);
    }

    public void saveRefreshToken(String refreshToken, String username){
        tokenRepository.save(new RefreshToken(refreshToken, username));
    }

    public String refreshAccessToken(String refreshToken){
        RefreshToken token = tokenRepository.findByToken(refreshToken).orElseThrow(() -> new JWTVerificationException("invalid or expired token"));
        String username = jwtUtils.getUsernameFromToken(refreshToken, jwtUtils.REFRESH);

        if(username==null){
            tokenRepository.delete(token);
            throw new JWTVerificationException("expired token");
        }

        return jwtUtils.generateAccessToken(username);
    }
}
