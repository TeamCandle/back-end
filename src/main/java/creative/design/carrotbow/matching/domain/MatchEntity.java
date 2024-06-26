package creative.design.carrotbow.matching.domain;

import creative.design.carrotbow.matching.domain.dto.type.MatchEntityStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class MatchEntity {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id")
    private Requirement requirement;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @Enumerated(EnumType.STRING)
    private MatchEntityStatus status;

    private LocalDateTime createTime;


    public MatchEntity(Long id) {
        this.id = id;
    }

    @Builder
    public MatchEntity(Requirement requirement, Application application, MatchEntityStatus status, LocalDateTime createTime) {
        this.requirement = requirement;
        this.application = application;
        this.status = status;
        this.createTime = createTime;
    }

    public void changeStatus(MatchEntityStatus status){
        this.status= status;
    }

}
