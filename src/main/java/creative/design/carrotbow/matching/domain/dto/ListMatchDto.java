package creative.design.carrotbow.matching.domain.dto;

import creative.design.carrotbow.matching.domain.dto.type.CareType;
import lombok.Builder;
import lombok.Data;

@Data
public class ListMatchDto {

    private Long id;
    private byte[] image;

    private String breed;

    private String careType;

    private String status;

    @Builder
    public ListMatchDto(Long id, byte[] image, String breed, String careType, String status) {
        this.id = id;
        this.image = image;
        this.breed = breed;
        this.careType = careType;
        this.status = status;
    }
}
