package creative.design.carrotbow.profile.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ListDogDto {
    private Long id;
    private String name;
    private String breed;
    private String gender;
    private byte[] image;

    @Builder
    public ListDogDto(Long id, String breed, String name, String gender, byte[] image) {
        this.id = id;
        this.breed = breed;
        this.name = name;
        this.gender = gender;
        this.image = image;
    }
}
