package creative.design.carrotbow.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User reviewedUser;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private MatchEntity match;

    private String text;

    private LocalDateTime createdAt;

    private float rating;

    public void change(String text, float rating){
        this.text=text;
        this.rating=rating;
    }
    @Builder
    public Review(User reviewedUser, MatchEntity match, String text, LocalDateTime createdAt, float rating) {
        this.reviewedUser = reviewedUser;
        this.match = match;
        this.text = text;
        this.createdAt = createdAt;
        this.rating = rating;
    }
}
