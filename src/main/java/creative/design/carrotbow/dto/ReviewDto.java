package creative.design.carrotbow.dto;

import creative.design.carrotbow.domain.CareType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDto {

    private Long matchId;
    private String text;
    private float rating;
    private LocalDateTime createdAt;
    private String breed;
    private CareType careType;

    @Builder
    public ReviewDto(Long id, String text, float rating, LocalDateTime createdAt, String breed, CareType careType) {
        this.matchId = id;
        this.text = text;
        this.rating = rating;
        this.createdAt = createdAt;
        this.breed = breed;
        this.careType = careType;
    }
}
