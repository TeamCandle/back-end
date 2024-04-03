package creative.design.carrotbow.domain;

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
    private String age;
    private Integer size;
    private Integer weight;
    private String breed;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    public void setMember(User owner){
        this.owner = owner;
        owner.getDogs().add(this);
    }

    @Builder
    public Dog(String name, String gender, Boolean neutered, String age, Integer size, Integer weight, String breed, String description, User owner) {
        this.name = name;
        this.gender = gender;
        neutered = neutered;
        this.age = age;
        this.size = size;
        this.weight = weight;
        this.breed = breed;
        this.description = description;
        this.owner = owner;
    }
}
