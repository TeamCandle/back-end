package creative.design.carrotbow.profile.domain;

import creative.design.carrotbow.profile.domain.dto.DogRegisterForm;
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
    private boolean neutered;
    private int age;
    private float size;
    private float weight;
    private String breed;
    private String description;
    private String image;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    public void setOwner(User owner){
        this.owner = owner;
    }

    public void delete(){
        this.deleted=true;
    }

    public void changeAttr(DogRegisterForm dogEdition, String image){
        this.name = dogEdition.getName();
        this.age = dogEdition.getAge();
        this.gender = dogEdition.getGender();
        this.neutered = dogEdition.isNeutered();
        this.breed = dogEdition.getBreed();
        this.size = dogEdition.getSize();
        this.weight = dogEdition.getWeight();
        this.description = dogEdition.getDescription();
        this.image = image;
    }

    @Builder
    public Dog(String name, String gender, boolean neutered, int age, float size, float weight, String breed, String description, String image) {
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
