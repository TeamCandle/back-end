package creative.design.carrotbow.dto;

import lombok.Builder;
import lombok.Data;


@Data
public class UserProfileDto {
    private String name;
    private String gender;
    private int age;
    private String description;
    private String image;


    @Builder
    public UserProfileDto(String name, String gender, int age, String description, String image) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.description = description;
        this.image = image;
    }
}
