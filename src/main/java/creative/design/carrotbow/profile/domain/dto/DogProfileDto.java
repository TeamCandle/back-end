package creative.design.carrotbow.profile.domain.dto;


import lombok.Builder;
import lombok.Data;

@Data
public class DogProfileDto {
    private Long id;
    private String name;
    private Long owner;
    private String gender;
    private boolean neutered;
    private int age;
    private float size;
    private float weight;
    private String breed;
    private String description;

    private byte[] image;

    @Builder
    public DogProfileDto(Long id, String name, Long owner, String gender, boolean neutered, int age, float size, float weight, String breed, String description, byte[] image) {
        this.id = id;
        this.name = name;
        this.owner = owner;
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
