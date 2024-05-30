package creative.design.carrotbow.profile.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import creative.design.carrotbow.DummyUtils;
import creative.design.carrotbow.error.InvalidAccessException;
import creative.design.carrotbow.profile.domain.User;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.profile.service.UserService;
import creative.design.carrotbow.security.jwt.JwtUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final DummyUtils dummyUtils;


    //dummy - 테스트용
    @PostConstruct
    public void makeDummy(){
        dummyUtils.makeDummy();
    }

    @GetMapping("/dummy")
    @ResponseBody
    public String getUser(@RequestParam Long id){
        User user = userService.find(id);

        return dummyUtils.makeDummyToken(user.getUsername());
    }


    @GetMapping("/login/kakao")
    public String loginKakao(){
        return "redirect:/oauth2/authorization/kakao";
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logOut(@AuthenticationPrincipal PrincipalDetails principalDetails){
        AuthenticationUser user = principalDetails.getUser();
        userService.logout(user);
        return ResponseEntity.status(HttpStatus.OK).body("success logout ");
    }

    @PostMapping("/accessToken")
    public ResponseEntity<?> refreshAccessToken(@RequestBody(required = false) JSONObject jsonRequest, @AuthenticationPrincipal PrincipalDetails principalDetails){

        String refreshToken = jsonRequest != null ? (String)jsonRequest.get("refreshToken") : null;

        if(refreshToken==null) {
            throw new JWTVerificationException("can't find refreshToken");
        }

        HashMap<String, String> keys = userService.refreshAccessToken(refreshToken, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(keys);

    }


}
