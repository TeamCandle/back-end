package creative.design.carrotbow.matching.domain;

import creative.design.carrotbow.matching.domain.dto.type.MatchStatus;
import creative.design.carrotbow.profile.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Application {

    public static String WAITING="수락 대기중";

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id")
    private Requirement requirement;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    private String description;

    private LocalDateTime createTime;

    public void apply(Requirement requirement){
        this.requirement = requirement;
        requirement.getApplications().add(this);
    }

    public void changeStatus(MatchStatus status){
        this.status = status;
    }

    public String getActualStatus(){
        return this.status==MatchStatus.NOT_MATCHED&&requirement.getActualStatus().equals(Requirement.RECRUITING)?WAITING:this.status.getActualName();
    }

    @Builder
    public Application(User user, MatchStatus status, String description, LocalDateTime createTime) {
        this.user = user;
        this.status = status;
        this.description = description;
        this.createTime = createTime;
    }
}


