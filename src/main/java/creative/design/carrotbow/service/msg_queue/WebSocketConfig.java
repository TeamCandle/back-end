package creative.design.carrotbow.service.msg_queue;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatInterceptor chatInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.setPathMatcher(new AntPathMatcher(".")); // URL을 / -> .으로
        registry.setApplicationDestinationPrefixes("/send");  //  @MessageMapping 메서드로 라우팅된다.  Client에서 SEND 요청을 처리
        registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue");

        //Spring Messaging에서 STOMP 브로커 릴레이 기능을 활성화
        //STOMP 브로커 릴레이는 Spring Messaging 애플리케이션이 외부 STOMP 브로커와 통신할 수 있도록 함
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatInterceptor);
    }
}
