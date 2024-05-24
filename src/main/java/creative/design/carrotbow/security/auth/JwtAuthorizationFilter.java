package creative.design.carrotbow.security.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import creative.design.carrotbow.profile.domain.User;
import creative.design.carrotbow.security.jwt.JwtUtils;
import creative.design.carrotbow.profile.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JwtAuthorizationFilter.JwtAuthorizationFilter");

        String jwtHeader = request.getHeader("Authorization");

        if(jwtHeader != null && jwtHeader.startsWith("Bearer")){
            String jwtToken = jwtHeader.replace("Bearer ", "");
            String username = jwtUtils.getUsernameFromToken(jwtToken, JwtUtils.ACCESS);

            if(username == null){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                Map<String, Object> body = new HashMap<>();
                body.put("status", 401);
                body.put("error", "Unauthorized");
                body.put("message", "Invalid or expired token");

                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(body));
            }
            else {
                User userEntity = userService.findByUsername(username);
                PrincipalDetails principalDetails = new PrincipalDetails(
                        AuthenticationUser.builder()
                                .id(userEntity.getId())
                                .username(userEntity.getUsername())
                                .password(userEntity.getPassword())
                                .email(userEntity.getEmail())
                                .role(userEntity.getRole())
                                .build());
                Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
        }
        else {
            filterChain.doFilter(request, response);
        }
    }
}
