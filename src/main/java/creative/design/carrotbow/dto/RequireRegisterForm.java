package creative.design.carrotbow.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

@Data
public class RequireRegisterForm {

    private Long id;
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
    @NotEmpty
    private String description;
}
