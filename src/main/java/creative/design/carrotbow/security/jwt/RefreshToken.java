package creative.design.carrotbow.security.jwt;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
    private String username;

    public RefreshToken(String token, String username) {
        this.token = token;
        this.username = username;
    }
}
