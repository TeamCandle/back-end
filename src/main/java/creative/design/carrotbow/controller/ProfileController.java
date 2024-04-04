package creative.design.carrotbow.controller;

import creative.design.carrotbow.dto.UserProfileDto;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Controller
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
    public ResponseEntity changeUserDescription(@RequestBody String description, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        profileService.changeUserDescription(principalDetails.getUser(), description);
        return ResponseEntity.status(HttpStatus.OK).body("success change description");
    }

    @PostMapping(value = "/profile/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity changeUserImage(@RequestPart("image") MultipartFile image, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        profileService.changeUserImage(principalDetails.getUser(), image);
        System.out.println("success!");
        return ResponseEntity.status(HttpStatus.OK).body("success change image");
    }
}
