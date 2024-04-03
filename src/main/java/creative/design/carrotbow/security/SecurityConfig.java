package creative.design.carrotbow.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import creative.design.carrotbow.security.jwt.JwtUtils;
import creative.design.carrotbow.security.oauth2.AuthenticationFailureHandler;
import creative.design.carrotbow.security.oauth2.AuthenticationSuccessHandler;
import creative.design.carrotbow.security.auth.JwtAuthorizationFilter;
import creative.design.carrotbow.security.oauth2.PrincipalOauth2UserService;
import creative.design.carrotbow.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(cs->cs.disable())
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(fl->fl.disable())
                .httpBasic(hb->hb.disable())
                .authorizeHttpRequests(ahr-> ahr.requestMatchers("/profile/**", "/user/logout").hasAnyRole("USER")
                        .anyRequest().permitAll())
                .oauth2Login(oauth2Login -> oauth2Login
                        .successHandler(new AuthenticationSuccessHandler(jwtUtils, userService))
                        .failureHandler(new AuthenticationFailureHandler())
                        .userInfoEndpoint(endpoint ->
                                endpoint.userService(principalOauth2UserService)))
                .addFilterBefore(new JwtAuthorizationFilter(userService, jwtUtils), OAuth2LoginAuthenticationFilter.class)
                .exceptionHandling(handling ->
                        handling
                                .authenticationEntryPoint((request, response, authException) ->{
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json");

                                    Map<String, Object> body = new HashMap<>();
                                    body.put("status", 401);
                                    body.put("error", "Unauthorized");
                                    body.put("message", "Not logged in");

                                    ObjectMapper mapper = new ObjectMapper();
                                    response.getWriter().write(mapper.writeValueAsString(body));})
                                .accessDeniedHandler(((request, response, accessDeniedException) -> {response.sendError(HttpServletResponse.SC_FORBIDDEN);})))
                .build();
    }

}
