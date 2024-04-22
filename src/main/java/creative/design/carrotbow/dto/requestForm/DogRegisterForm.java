package creative.design.carrotbow.dto.requestForm;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class DogRegisterForm {

    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String gender;
    private boolean neutered;
    @Min(0)
    private int age;
    @DecimalMin(inclusive = false, value = "0.0")
    private float size;
    @DecimalMin(inclusive = false, value = "0.0")
    private float weight;
    @NotEmpty
    private String breed;
    private String description;
    private MultipartFile image;


    public DogRegisterForm(String name, String gender, boolean neutered, int age, float size, float weight, String breed, String description, MultipartFile image) {
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
