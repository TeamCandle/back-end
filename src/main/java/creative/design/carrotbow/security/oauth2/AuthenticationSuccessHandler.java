package creative.design.carrotbow.security.oauth2;

import creative.design.carrotbow.security.auth.AuthenticationUser;
import creative.design.carrotbow.security.jwt.JwtUtils;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.profile.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private JwtUtils jwtUtils;

    private UserService userService;


    public AuthenticationSuccessHandler(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        AuthenticationUser user = principalDetails.getUser();

        String accessToken = jwtUtils.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        userService.saveRefreshToken(refreshToken, user);

        response.addHeader("accessToken", "Bearer "+accessToken);
        response.addHeader("refreshToken", "Bearer "+refreshToken);

        String script = "<script>" +
                "var tokens = {" +
                "accessToken: '" + accessToken + "'," +
                "refreshToken: '" + refreshToken + "'" +
                "};" +
                "tokenHandler.postMessage(JSON.stringify(tokens));" +
                "</script>";
        response.setContentType("text/html");

        response.getWriter().write(script);
    }
}
