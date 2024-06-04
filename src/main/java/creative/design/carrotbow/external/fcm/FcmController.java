package creative.design.carrotbow.external.fcm;

import com.google.firebase.messaging.FirebaseMessagingException;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j(topic = "ACCESS_LOG")
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    /*
    @PostMapping("/message/fcm/topic")
    public ResponseEntity<?> sendMessageTopic(@RequestBody MessageRequestDTO requestDTO) throws IOException, FirebaseMessagingException {
        fcmService.sendMessageByTopic(requestDTO.getTitle(), requestDTO.getBody());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/message/fcm/token")
    public ResponseEntity<?> sendMessageToken(@RequestBody MessageRequestDTO requestDTO) throws IOException, FirebaseMessagingException{
        fcmService.sendMessageByToken(requestDTO.getTitle(), requestDTO.getBody(), requestDTO.getTargetToken());
        return ResponseEntity.ok().build();
    }
     */


    @PostMapping("/token")
    public ResponseEntity<?> registerToken(@RequestBody(required = false) JSONObject jsonRequest, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("POST /fcm/token");

        String fcmToken = jsonRequest != null ? (String)jsonRequest.get("token") : null;
        Long userId = principalDetails.getUser().getId();

        Long tokenId = fcmService.saveToken(fcmToken, userId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", tokenId);
        return ResponseEntity.ok().body(result);
    }

}
