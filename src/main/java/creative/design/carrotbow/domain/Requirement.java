package creative.design.carrotbow.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Requirement {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="dog_id")
    private Dog dog;

    @Enumerated(EnumType.STRING)
    private CareType careType;

    private LocalDateTime careTime;

    private Point careLocation;

    private String description;

    @Enumerated(EnumType.STRING)
    private RequirementStatus status;

    private LocalDateTime createTime;

    @OneToMany(mappedBy = "requirement")
    List<Application> applications = new ArrayList<>();

    public void changeStatus(RequirementStatus status){
        this.status = status;
    }

    @Builder
    public Requirement(User user, Dog dog, CareType careType, LocalDateTime careTime, Point careLocation, String description, RequirementStatus status, LocalDateTime createTime) {
        this.user = user;
        this.dog = dog;
        this.careType = careType;
        this.careTime = careTime;
        this.careLocation = careLocation;
        this.description = description;
        this.status = status;
        this.createTime = createTime;
    }
}
