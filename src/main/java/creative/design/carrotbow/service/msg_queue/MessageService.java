package creative.design.carrotbow.service.msg_queue;

import creative.design.carrotbow.domain.MatchEntity;
import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.error.InvalidAccessException;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;


    private final MatchService matchService;

    @Transactional
    public Long recordMessage(String message, Long userId, Long roomId){
        return messageRepository.save(Message.builder()
                .message(message)
                .sender(new User(userId))
                .room(new MatchEntity(roomId))
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<MessageDto> getRecords(Long roomId, AuthenticationUser authenticationUser){

        MatchEntity room = matchService.getMatch(roomId);

        String username = authenticationUser.getUsername();

        if(!username.equals(room.getRequirement().getUser().getUsername())&&!username.equals(room.getApplication().getUser().getUsername())){
            throw new InvalidAccessException("Invalid access");
        }

        List<Message> msgList = messageRepository.findMsgListByRoom(roomId);

        return msgList.stream().map(msg-> new MessageDto(msg.getMessage(), msg.getSender().getUsername(), msg.getCreatedAt())).collect(Collectors.toList());
    }
}
