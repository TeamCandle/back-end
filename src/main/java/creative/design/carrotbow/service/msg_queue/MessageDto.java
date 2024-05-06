package creative.design.carrotbow.service.msg_queue;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
public class MessageDto {

    private String message;
    private String sender;
    private LocalDateTime createAt;

    public MessageDto(String message, String sender, LocalDateTime createAt) {
        this.message = message;
        this.sender = sender;
        this.createAt = createAt;
    }
}
