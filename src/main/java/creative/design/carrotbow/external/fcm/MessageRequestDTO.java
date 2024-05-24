package creative.design.carrotbow.external.fcm;

import lombok.Builder;
import lombok.Data;

//Test용 dto
@Data
public class MessageRequestDTO {

    private String title;
    private String body;
    private String targetToken;

    @Builder
    public MessageRequestDTO(String title, String body, String targetToken) {
        this.title = title;
        this.body = body;
        this.targetToken = targetToken;
    }
}
