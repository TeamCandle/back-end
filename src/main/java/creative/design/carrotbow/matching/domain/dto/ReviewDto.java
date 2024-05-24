package creative.design.carrotbow.matching.domain.dto;

import creative.design.carrotbow.matching.domain.dto.type.CareType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDto {

    private Long id;
    private String text;
    private float rating;
    private LocalDateTime createdAt;
    private String breed;
    private String careType;

    @Builder
    public ReviewDto(Long id, String text, float rating, LocalDateTime createdAt, String breed, String careType) {
        this.id=id;
        this.text = text;
        this.rating = rating;
        this.createdAt = createdAt;
        this.breed = breed;
        this.careType = careType;
    }
}
