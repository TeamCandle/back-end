package creative.design.carrotbow.error;

import lombok.Builder;
import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private String field;
    private Object rejectedValue;
    private String code;

    @Builder
    public ErrorResponse(String message, String field, Object rejectedValue, String code) {
        this.message = message;
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.code = code;
    }
}
