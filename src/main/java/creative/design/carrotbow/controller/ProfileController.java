package creative.design.carrotbow.controller;

import creative.design.carrotbow.dto.UserProfileDto;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Controller
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping({"","/"})
    @ResponseBody
    public String home(){
        return "home";
    }

    @GetMapping("/profile/user")
    @ResponseBody
    public UserProfileDto getUserProfile(@AuthenticationPrincipal PrincipalDetails principalDetails){

        return profileService.getUserProfile(principalDetails.getUser());
    }

    @PostMapping("/profile/user")
    public void handleTextData(@RequestBody String description, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        profileService.changeUserDescription(principalDetails.getUser(), description);
    }

    @PostMapping(value = "/profile/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void handleImageData(@RequestPart("image") MultipartFile image, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        profileService.changeUserImage(principalDetails.getUser(), image);
    }
}
