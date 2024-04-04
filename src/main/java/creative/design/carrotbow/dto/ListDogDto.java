package creative.design.carrotbow.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ListDogDto {
    private Long id;
    private String name;
    private String gender;
    private byte[] image;

    @Builder
    public ListDogDto(Long id, String name, String gender, byte[] image) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.image = image;
    }
}
