package creative.design.carrotbow.matching.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ListMatchDto {

    private Long id;
    private byte[] image;

    private String breed;

    private String careType;

    private String time;

    private String status;

    @Builder
    public ListMatchDto(Long id, byte[] image, String breed, String careType, String time, String status) {
        this.id = id;
        this.image = image;
        this.breed = breed;
        this.time = time;
        this.careType = careType;
        this.status = status;
    }
}
