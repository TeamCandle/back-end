package creative.design.carrotbow.dto;

import creative.design.carrotbow.domain.CareType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDto {

    private Long id;
    private Long matchId;
    private String text;
    private float rating;
    private LocalDateTime createdAt;
    private String breed;
    private CareType careType;

    @Builder
    public ReviewDto(Long id, Long matchId, String text, float rating, LocalDateTime createdAt, String breed, CareType careType) {
        this.id=id;
        this.matchId = matchId;
        this.text = text;
        this.rating = rating;
        this.createdAt = createdAt;
        this.breed = breed;
        this.careType = careType;
    }
}
