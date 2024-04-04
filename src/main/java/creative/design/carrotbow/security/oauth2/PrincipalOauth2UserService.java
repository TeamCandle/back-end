package creative.design.carrotbow.security.oauth2;

import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.security.oauth2.provider.KakaoUserInfo;
import creative.design.carrotbow.security.oauth2.provider.Oauth2UserInfo;
import creative.design.carrotbow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //System.out.println("clientRegistration: " + userRequest.getClientRegistration());

        //System.out.println("accessToken: " + userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        //System.out.println("loadUser().getAttributes: " + oAuth2User.getAttributes());

        Oauth2UserInfo oauth2UserInfo = null;

        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("google login");
            //oauth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("naver login");
            //oauth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")){
            System.out.println("kakao login");
            oauth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }
        else{
            System.out.println("we support google, naver and kakao");
        }

        String provider = oauth2UserInfo.getProvider();
        String providerId = oauth2UserInfo.getProviderId();
        String username = provider+"_"+providerId;

        User user = userService.findRead(username);

        if(user==null){
            String password = bCryptPasswordEncoder.encode(UUID.randomUUID().toString());
            String email = oauth2UserInfo.getEmail();
            String phNum = oauth2UserInfo.getPhoneNumber();
            String name = oauth2UserInfo.getName();
            String gender = oauth2UserInfo.getGender();
            int birthYear = oauth2UserInfo.getBirthYear();

            String role = "ROLE_USER";

            user = User.builder().username(username).password(password).email(email)
                    .role(role).phNum(phNum).name(name).gender(gender).birthYear(birthYear)
                    .build();

            userService.register(user);
        }

        return new PrincipalDetails(
                AuthenticationUser.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .email(user.getEmail())
                        .role(user.getRole()).build()
                , oAuth2User.getAttributes());
    }
}

