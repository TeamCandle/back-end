package creative.design.carrotbow.matching.domain.dto.requestForm;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.geo.Point;

@Data
public class RequirementCondForm {
    @NotNull
    private Point location;
    @Range(max = 10)
    private int radius;
    private String dogSize;
    private String careType;
}
