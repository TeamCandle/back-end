package creative.design.carrotbow.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.UserService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity handleCustomException(JWTVerificationException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @GetMapping("/login/kakao")
    public String loginKakao(){
        return "redirect:/oauth2/authorization/kakao";
    }

    @DeleteMapping("/logout")
    public ResponseEntity logOut(@AuthenticationPrincipal PrincipalDetails principalDetails){
        String username = principalDetails.getUser().getUsername();
        userService.logOut(username);
        return ResponseEntity.status(HttpStatus.OK).body("success logout ");
    }

    @PostMapping("/accessToken")
    public ResponseEntity refreshAccessToken(@RequestBody(required = false) JSONObject jsonRequest){

        String refreshToken = jsonRequest != null ? (String)jsonRequest.get("refreshToken") : null;

        if(refreshToken==null) {
            throw new JWTVerificationException("can't find refreshToken");
        }

        String accessToken = userService.refreshAccessToken(refreshToken);

        Map<String, String> token = new HashMap<>();
        token.put("accessToken", "Bearer " + accessToken);

        return ResponseEntity.status(HttpStatus.OK).body(token);

    }


}
