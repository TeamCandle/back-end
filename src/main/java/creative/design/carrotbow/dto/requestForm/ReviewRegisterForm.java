package creative.design.carrotbow.dto.requestForm;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class ReviewRegisterForm {

    @NotNull
    private Long id;

    @Range(max = 5)
    private float rating;

    @NotEmpty
    private String text;
}
