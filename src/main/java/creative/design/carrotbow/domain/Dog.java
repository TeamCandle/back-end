package creative.design.carrotbow.domain;

import creative.design.carrotbow.dto.DogRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Dog {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String gender;
    private Boolean neutered;
    private Integer age;
    private Integer size;
    private Integer weight;
    private String breed;
    private String description;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    public void setOwner(User owner){
        this.owner = owner;
        owner.getDogs().add(this);
    }

    public void changeAttr(DogRequestDto dogEdition, String image){
        this.name = dogEdition.getName();
        this.age = dogEdition.getAge();
        this.gender = dogEdition.getGender();
        this.neutered = dogEdition.getNeutered();
        this.breed = dogEdition.getBreed();
        this.size = dogEdition.getSize();
        this.weight = dogEdition.getWeight();
        this.description = dogEdition.getDescription();
        this.image = image;
    }

    @Builder
    public Dog(String name, String gender, Boolean neutered, Integer age, Integer size, Integer weight, String breed, String description, String image) {
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
