package creative.design.carrotbow.chat.service;

import creative.design.carrotbow.chat.repository.MessageRepository;
import creative.design.carrotbow.chat.domain.Message;
import creative.design.carrotbow.chat.domain.MessageDto;
import creative.design.carrotbow.matching.domain.MatchEntity;
import creative.design.carrotbow.profile.domain.User;
import creative.design.carrotbow.error.InvalidAccessException;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import creative.design.carrotbow.matching.service.MatchService;
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
    public Long recordMessage(String message, AuthenticationUser user, Long roomId){
        return messageRepository.save(Message.builder()
                .message(message)
                .sender(new User(user.getId()))
                .room(new MatchEntity(roomId))
                .createdAt(LocalDateTime.now())
                .build());
    }

    public List<MessageDto> getRecords(Long roomId, AuthenticationUser user){

        MatchEntity room = matchService.getMatch(roomId);

        if(!user.getId().equals(room.getRequirement().getUser().getId())&&!user.getId().equals(room.getApplication().getUser().getId())){
            throw new InvalidAccessException("Invalid access");
        }

        List<Message> msgList = messageRepository.findMsgListByRoom(roomId);

        return msgList.stream().map(msg-> new MessageDto(msg.getMessage(), msg.getSender().getName(), msg.getCreatedAt())).collect(Collectors.toList());
    }
}
