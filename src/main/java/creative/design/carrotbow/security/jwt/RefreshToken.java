package creative.design.carrotbow.security.jwt;

import creative.design.carrotbow.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue
    private Long id;
    private String token;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public RefreshToken(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
