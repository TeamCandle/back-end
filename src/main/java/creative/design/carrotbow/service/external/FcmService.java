package creative.design.carrotbow.service.external;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.error.NotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmRepository fcmRepository;
    private final RedisService redisService;


    // 비밀키 경로 환경 변수 ( 필수 )
    @Value("${fcm.service-account-file}")
    private String serviceAccountFilePath;

    // topic 이름 환경 변수
    @Value("${fcm.topic-name}")
    private String topicName;

    // 프로젝트 아이디 환경 변수 ( 필수 )
    @Value("${fcm.project-id}")
    private String projectId;

    @PostConstruct
    public void initialize() throws IOException {
        //Firebase 프로젝트 정보를 FireBaseOptions에 입력해준다.
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(serviceAccountFilePath).getInputStream()))
                .setProjectId(projectId)
                .build();

        //입력한 정보를 이용하여 initialze 해준다.
        FirebaseApp.initializeApp(options);
    }


    // 해당 지정된 topic에 fcm를 보내는 메서드
    public void sendMessageByTopic(String title, String body) throws IOException, FirebaseMessagingException {

        FirebaseMessaging.getInstance().send(Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setTopic(topicName)
                .build());

    }

    // 받은 token을 이용하여 fcm를 보내는 메서드
    public void sendMessageByToken(String title, String body, String token) throws FirebaseMessagingException {
        /*FirebaseMessaging.getInstance().send(Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                        .putData("test", "test")
                .setToken(token)
                .build());*/

        FirebaseMessaging.getInstance().send(Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setToken(token)
                .build());
    }



    @Transactional(readOnly = true)
    public String getToken(Long userId){

        String token = redisService.getValues("user_"+userId);

        if(token==null){
            FcmToken fcmToken = fcmRepository.findByUser(userId).orElseThrow(() -> new NotFoundException("can't find FcmToken. userId:" + userId));
            token = fcmToken.getToken();
            redisService.setValues("user_"+userId, token);
        }

        return token;
    }

    @Transactional
    public Long saveToken(String token, Long userId){
        FcmToken fcmToken = fcmRepository.findByUser(userId).orElse(null);
        if(fcmToken==null){
            fcmToken=FcmToken.builder()
                    .token(token)
                    .user(new User(userId))
                    .build();

            fcmRepository.save(fcmToken);
        }else{
            fcmToken.changeToken(token);
        }

        redisService.setValues("user_"+userId, token);

        return fcmToken.getId();
    }

}
