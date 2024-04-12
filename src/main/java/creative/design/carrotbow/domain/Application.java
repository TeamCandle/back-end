package creative.design.carrotbow.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Application {
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
    private ApplicationStatus status;

    private String description;

    private LocalDateTime createTime;

    public void apply(Requirement requirement){
        this.requirement = requirement;
        requirement.getApplications().add(this);
    }

    public void changeStatus(ApplicationStatus status){
        this.status = status;
    }


    @Builder
    public Application(User user, ApplicationStatus status, String description, LocalDateTime createTime) {
        this.user = user;
        this.status = status;
        this.description = description;
        this.createTime = createTime;
    }
}


