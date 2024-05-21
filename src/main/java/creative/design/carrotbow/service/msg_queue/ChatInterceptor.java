package creative.design.carrotbow.service.msg_queue;


import creative.design.carrotbow.domain.MatchEntity;
import creative.design.carrotbow.domain.MatchEntityStatus;
import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.error.InvalidAccessException;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.security.jwt.JwtUtils;
import creative.design.carrotbow.service.MatchService;
import creative.design.carrotbow.service.UserService;
import creative.design.carrotbow.service.external.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatInterceptor implements ChannelInterceptor {

    private final MatchService matchService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final RedisService redisService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        System.out.println("preSend: " + accessor.getCommand());
        System.out.println("startAccessor: " + accessor);

        // 연결 요청시 JWT 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Authorization 헤더 추출
            String jwtHeader = accessor.getFirstNativeHeader("Authorization");
            System.out.println("jwtHeader: " + jwtHeader);

            if (jwtHeader != null && jwtHeader.startsWith("Bearer")) {
                String jwtToken = jwtHeader.replace("Bearer ", "");
                String username = jwtUtils.getUsernameFromToken(jwtToken, JwtUtils.ACCESS);

                System.out.println(username);

                if (username == null) {
                    throw new InvalidAccessException("Invalid or Expired token");
                } else {

                    User userEntity = userService.findByUsername(username);
                    PrincipalDetails principalDetails = new PrincipalDetails(
                            AuthenticationUser.builder()
                                    .id(userEntity.getId())
                                    .username(userEntity.getUsername())
                                    .password(userEntity.getPassword())
                                    .email(userEntity.getEmail())
                                    .role(userEntity.getRole())
                                    .build());


                    Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
                    accessor.getSessionAttributes().put("Authentication", authentication);
                }
            }
            else{
                throw new InvalidAccessException("Invalid access");
            }
        }


        // 구독 요청인 경우
        if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = (String) message.getHeaders().get("simpDestination");
            Long roomId = Long.parseLong(destination.replace("/exchange/chat.exchange/*.room.", ""));
            MatchEntity match = matchService.getMatch(roomId);

            Long requirePerson = match.getRequirement().getUser().getId();
            Long applyPerson = match.getApplication().getUser().getId();

            System.out.println("roomId: " + roomId);

            if(match.getStatus()==MatchEntityStatus.COMPLETED || match.getStatus() == MatchEntityStatus.CANCELLED){
                throw new InvalidAccessException("Invalid access");
            }

            Authentication authentication = (Authentication) accessor.getSessionAttributes().get("Authentication");
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            AuthenticationUser user = principalDetails.getUser();

            System.out.println("username: " + user.getUsername());

            String targetToken;

            if(user.getId().equals(requirePerson)){
                targetToken = redisService.getValues("user_"+applyPerson);
            }else if(user.getId().equals(applyPerson)){
                targetToken = redisService.getValues("user_"+requirePerson);
            }
            else{
                throw new InvalidAccessException("Invalid access");
            }

            accessor.getSessionAttributes().put("roomId", roomId);
            accessor.getSessionAttributes().put("target", targetToken);

            //redis에 추가
            redisService.addSets("room_"+roomId, user.getId().toString());
        }


        // 메시지 전송인 경우
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            System.out.println(accessor);

            String destination = (String) message.getHeaders().get("simpDestination");
            Long targetId = Long.parseLong(destination.replace("/send/chat.talk.", ""));

            Long roomId = (Long) accessor.getSessionAttributes().get("roomId");

            if(!targetId.equals(roomId)){
                throw new InvalidAccessException("Invalid access");
            }
        }

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {

            Authentication authentication = (Authentication) accessor.getSessionAttributes().get("Authentication");
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            AuthenticationUser user = principalDetails.getUser();

            Long roomId = (Long) accessor.getSessionAttributes().get("roomId");

            //redis에서 삭제
            redisService.deleteSets("room_"+roomId, user.getId().toString());
        }

        return message;
    }
}
