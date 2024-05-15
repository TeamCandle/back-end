package creative.design.carrotbow.dto.requestForm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

@Data
public class RequireRegisterForm {
    @NotNull
    private Long dogId;
    @NotEmpty
    private String careType;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
    @NotNull
    private Point careLocation;
    @Min(0)
    private int reward;
    @NotEmpty
    private String description;
}
