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


    @Builder
    public AuthenticationUser(Long id, String username, String password, String email, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
