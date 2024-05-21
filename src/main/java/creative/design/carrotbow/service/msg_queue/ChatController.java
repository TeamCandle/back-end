package creative.design.carrotbow.service.msg_queue;

import com.google.firebase.messaging.FirebaseMessagingException;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.MatchService;
import creative.design.carrotbow.service.external.FcmService;
import creative.design.carrotbow.service.external.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
@RequiredArgsConstructor
public class ChatController {

    private final RabbitTemplate rabbitTemplate;
    private final MessageService messageService;
    private final RedisService redisService;
    private final FcmService fcmService;





    // 이전 기록
    @ResponseBody
    @GetMapping("/chat/history")
    public ResponseEntity<?> getHistory(@RequestParam Long roomId, @AuthenticationPrincipal PrincipalDetails principalDetails){
        List<MessageDto> messages = messageService.getRecords(roomId, principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("messages", messages);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @EventListener
    public void handleSessionConnectedEvent(SessionConnectedEvent event) {
        System.out.println("event: " + event.getSource().toString());
    }
    @EventListener(SessionDisconnectEvent.class)
    public void handleDisconnectEvent(SessionDisconnectEvent event){
        System.out.println("event: " + event.getSource().toString());
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
    public String talkUser(@DestinationVariable("roomId") Long roomId, @Payload String message, StompHeaderAccessor accessor) throws FirebaseMessagingException {

        Authentication authentication = (Authentication) accessor.getSessionAttributes().get("Authentication");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        AuthenticationUser user = principalDetails.getUser();

        messageService.recordMessage(message, user, roomId);
        MessageDto messageDto = new MessageDto(message, user.getUsername(), LocalDateTime.now());

        Set<Object> members = redisService.getSets("room_" + roomId);

        if(members.size()>1){
            rabbitTemplate.convertAndSend("chat.exchange", "*.room." + roomId, messageDto);
        }else{
            String token = (String) accessor.getSessionAttributes().get("target");
            fcmService.sendMessageByToken(user.getUsername() + " message", message, token);
        }


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
