package creative.design.carrotbow.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ListAppDto {

    private Long id;
    private Long userId;
    private byte[] image;
    private String name;
    private String gender;
    private float rating;

    @Builder
    public ListAppDto(Long id, Long userId, byte[] image, String name, String gender, float rating) {
        this.id = id;
        this.userId = userId;
        this.image = image;
        this.name = name;
        this.gender = gender;
        this.rating = rating;
    }
}
