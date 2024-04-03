package creative.design.carrotbow.security.auth;


import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthenticationUser {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role;
    private String provider;
    private String providerId;

    @Builder
    public AuthenticationUser(Long id, String username, String password, String email, String role, String provider, String providerId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }
}
