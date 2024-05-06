package creative.design.carrotbow.service.msg_queue;

import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RabbitTemplate rabbitTemplate;
    private final MessageService messageService;

    // 이전 기록
    @ResponseBody
    @GetMapping("/chat/history")
    public List<MessageDto> getHistory(@RequestParam Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails){
        return messageService.getRecords(roomId, principalDetails.getUser());
    }


    // 채팅방 입장
    @MessageMapping("chat.enter.{roomId}")
    public Message enterUser(@DestinationVariable("roomId") Long roomId, @Payload Message message) {
        //message.setMessage(message.getSender() + "님이 채팅방에 입장하였습니다.");
        rabbitTemplate.convertAndSend("chat.exchange", "enter.room." + roomId, message);
        return message;
    }

    // 채팅방 대화
    @MessageMapping("chat.talk.{roomId}")
    public String talkUser(@DestinationVariable("roomId") Long roomId, @Payload String message, StompHeaderAccessor accessor) {

        Authentication authentication = (Authentication) accessor.getSessionAttributes().get("Authentication");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        messageService.recordMessage(message, principalDetails.getUser().getId(), roomId);

        MessageDto messageDto = new MessageDto(message, principalDetails.getName(), LocalDateTime.now());

        rabbitTemplate.convertAndSend("chat.exchange", "*.room." + roomId, messageDto);
        return message;
    }

    // 채팅방 퇴장
    @MessageMapping("chat.exit.{roomId}")
    public Message exitUser(@DestinationVariable("roomId") Long roomId, @Payload Message message){
        //message.setMessage(message.getSender() + "님이 채팅방에 퇴장하였습니다.");
        rabbitTemplate.convertAndSend("chat.exchange", "exit.room." + roomId, message);
        return message;
    }
}
