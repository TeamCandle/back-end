package creative.design.carrotbow.external.fcm;

import creative.design.carrotbow.profile.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FcmToken {

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void changeToken(String token){
        this.token=token;
    }

    @Builder
    public FcmToken(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
