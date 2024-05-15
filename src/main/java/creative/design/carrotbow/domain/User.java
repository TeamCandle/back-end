package creative.design.carrotbow.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role;


    private String name;
    private String gender;
    private int birthYear;
    private String phNum;
    private String description;
    private String image;
    private float totalRating;
    private int reviewCount;


    @OneToMany(mappedBy = "owner",  cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<Dog> dogs = new ArrayList<>();

    public User(Long id) {
        this.id = id;
    }

    @Builder
    public User(String username, String password, String email, String role, String name, String gender, int birthYear, String phNum, float totalRating, int reviewCount) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.name = name;
        this.gender = gender;
        this.birthYear = birthYear;
        this.phNum = phNum;
        this.totalRating = totalRating;
        this.reviewCount = reviewCount;
    }

    public void changeDescription(String description){
        this.description = description;
    }

    public void changeImage(String image){
        this.image = image;
    }

    public void addReview(float rating){
        reviewCount++;
        totalRating += rating;
    }

    public void subReview(float rating){
        reviewCount--;
        totalRating -= rating;
    }

    public float getRating(){
        return totalRating/reviewCount;
    }
}
