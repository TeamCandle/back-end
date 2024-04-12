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
    private String rate;

    @Builder
    public ListAppDto(Long id, Long userId, byte[] image, String name, String gender, String rate) {
        this.id = id;
        this.userId = userId;
        this.image = image;
        this.name = name;
        this.gender = gender;
        this.rate = rate;
    }
}
