package creative.design.carrotbow.dto;

import creative.design.carrotbow.domain.CareType;
import lombok.Builder;
import lombok.Data;

@Data
public class ListMatchDto {

    private Long id;
    private byte[] image;

    private String breed;

    private CareType careType;

    private String status;

    @Builder
    public ListMatchDto(Long id, byte[] image, String breed, CareType careType, String status) {
        this.id = id;
        this.image = image;
        this.breed = breed;
        this.careType = careType;
        this.status = status;
    }
}
