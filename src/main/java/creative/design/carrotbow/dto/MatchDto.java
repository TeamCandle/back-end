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
    private LocalDateTime careTime;
    private Point careLocation;
    private String description;
    private String userName;
    private Long dogId;
    private String status;


    @Builder
    public MatchDto(Long id, byte[] dogImage, CareType careType, LocalDateTime careTime, Point careLocation, String description, String userName, Long dogId, String status) {
        this.id = id;
        this.dogImage = dogImage;
        this.careType = careType;
        this.careTime = careTime;
        this.careLocation = careLocation;
        this.description = description;
        this.userName = userName;
        this.dogId = dogId;
        this.status = status;
    }
}
