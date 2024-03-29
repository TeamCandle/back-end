package creative.design.carrotbow.controller;

import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.dto.UserProfileDto;
import creative.design.carrotbow.repository.UserRepository;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;

@Controller
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @GetMapping({"","/"})
    @ResponseBody
    public String home(){
        return "home";
    }

    @GetMapping("/profile/user")
    @ResponseBody
    public UserProfileDto getUserProfile(@AuthenticationPrincipal PrincipalDetails principalDetails){

        User user = principalDetails.getUser();

        int age = LocalDate.now().getYear() - user.getBirthYear() + 1;

        return UserProfileDto
                .builder()
                .name(user.getName())
                .gender(user.getGender())
                .age(age)
                .description(user.getDescription()).build();
    }
}
