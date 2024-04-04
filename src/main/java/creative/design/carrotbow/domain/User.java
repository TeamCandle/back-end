package creative.design.carrotbow.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role;
    private String provider;
    private String providerId;

    private String name;
    private String gender;
    private int birthYear;
    private String phNum;
    private String description;
    private String image;

    @CreationTimestamp
    private LocalDateTime CreateDate;

    @OneToMany(mappedBy = "owner")
    private List<Dog> dogs = new ArrayList<>();

    @Builder
    public User(String username, String password, String email, String role, String provider, String providerId, String name, String gender, int birthYear, String phNum) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.name = name;
        this.gender = gender;
        this.birthYear = birthYear;
        this.phNum = phNum;
    }

    public void changeDescription(String description){
        this.description = description;
    }

    public void changeImage(String image){
        this.image = image;
    }
}
