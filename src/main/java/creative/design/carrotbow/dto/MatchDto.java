package creative.design.carrotbow.dto;

import creative.design.carrotbow.domain.CareType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

@Data
public class MatchDto {


    private Long id;
    private byte[] dogImage;
    private CareType careType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Point careLocation;
    private String description;
    private Long userId;
    private Integer reward;
    private Long dogId;
    private String status;


    @Builder
    public MatchDto(Long id, byte[] dogImage, Integer reward, CareType careType, LocalDateTime startTime, LocalDateTime endTime, Point careLocation, String description, Long userId, Long dogId, String status) {
        this.id = id;
        this.dogImage = dogImage;
        this.careType = careType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.careLocation = careLocation;
        this.description = description;
        this.userId = userId;
        this.reward = reward;
        this.dogId = dogId;
        this.status = status;
    }
}
