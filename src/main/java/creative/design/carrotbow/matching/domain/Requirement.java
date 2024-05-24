package creative.design.carrotbow.matching.domain;


import creative.design.carrotbow.matching.domain.dto.type.CareType;
import creative.design.carrotbow.matching.domain.dto.type.MatchStatus;
import creative.design.carrotbow.profile.domain.Dog;
import creative.design.carrotbow.profile.domain.User;
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

    public static String RECRUITING = "모집중";

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

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Point careLocation;

    private int reward;

    private String description;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    private LocalDateTime createTime;

    @OneToMany(mappedBy = "requirement")
    List<Application> applications = new ArrayList<>();

    public void changeStatus(MatchStatus status){
        this.status = status;
    }

    public String getActualStatus(){
        return this.status==MatchStatus.NOT_MATCHED&&LocalDateTime.now().isBefore(this.startTime)?RECRUITING:this.status.getActualName();
    }

    @Builder
    public Requirement(User user, Dog dog, int reward, CareType careType, LocalDateTime startTime, LocalDateTime endTime, Point careLocation, String description, MatchStatus status, LocalDateTime createTime) {
        this.user = user;
        this.dog = dog;
        this.careType = careType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.careLocation = careLocation;
        this.reward = reward;
        this.description = description;
        this.status = status;
        this.createTime = createTime;
    }
}
