package creative.design.carrotbow.chat.domain;


import creative.design.carrotbow.matching.domain.MatchEntity;
import creative.design.carrotbow.profile.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private MatchEntity room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User sender;

    private LocalDateTime createdAt;


    @Builder
    public Message(String message, MatchEntity room, User sender, LocalDateTime createdAt) {
        this.message = message;
        this.room = room;
        this.sender = sender;
        this.createdAt = createdAt;
    }
}