package creative.design.carrotbow.profile.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;


@Data
public class UserProfileDto {

    private Long id;
    private String name;
    private String gender;
    private int age;
    private float rating;
    private String description;
    private byte[] image;

    private ArrayList<ListDogDto> dogList;

    @Builder
    public UserProfileDto(Long id, String name, String gender, int age, float rating, String description, byte[] image, ArrayList<ListDogDto> dogList) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.rating = rating;
        this.description = description;
        this.image = image;
        this.dogList = dogList;
    }
}
