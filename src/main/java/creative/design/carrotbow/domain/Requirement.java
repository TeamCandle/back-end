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

    public static String RECRUITING = "RECRUITING";

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
        return this.status==MatchStatus.NOT_MATCHED&&LocalDateTime.now().isBefore(this.startTime)?RECRUITING:this.status.toString();
    }

    @Builder
    public Requirement(User user, Dog dog, CareType careType, LocalDateTime startTime, LocalDateTime endTime, Point careLocation, String description, MatchStatus status, LocalDateTime createTime) {
        this.user = user;
        this.dog = dog;
        this.careType = careType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.careLocation = careLocation;
        this.description = description;
        this.status = status;
        this.createTime = createTime;
    }
}
