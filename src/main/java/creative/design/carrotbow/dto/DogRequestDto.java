package creative.design.carrotbow.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class DogRequestDto {

    private Long id;
    private String name;
    private String gender;
    private Boolean neutered;
    private Integer age;
    private Integer size;
    private Integer weight;
    private String breed;
    private String description;
    private MultipartFile image;


    public DogRequestDto(String name, String gender, Boolean neutered, Integer age, Integer size, Integer weight, String breed, String description, MultipartFile image) {
        this.name = name;
        this.gender = gender;
        this.neutered = neutered;
        this.age = age;
        this.size = size;
        this.weight = weight;
        this.breed = breed;
        this.description = description;
        this.image = image;
    }
}
