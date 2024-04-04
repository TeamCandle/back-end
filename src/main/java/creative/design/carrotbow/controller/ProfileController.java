package creative.design.carrotbow.controller;

import creative.design.carrotbow.dto.DogProfileDto;
import creative.design.carrotbow.dto.DogRequestDto;
import creative.design.carrotbow.dto.UserProfileDto;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.ProfileService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PatchMapping("/profile/user")
    public ResponseEntity<String> changeUserDescription(@RequestBody(required = false) JSONObject jsonRequest, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String description = jsonRequest != null ? (String)jsonRequest.get("description") : null;

        profileService.changeUserDescription(principalDetails.getUser(), description);
        return ResponseEntity.status(HttpStatus.OK).body("success change description");
    }

    @PostMapping(value = "/profile/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> changeUserImage(@RequestPart("image") MultipartFile image, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        profileService.changeUserImage(principalDetails.getUser(), image);
        return ResponseEntity.status(HttpStatus.OK).body("success change image");
    }


    @GetMapping("/profile/dog")
    @ResponseBody
    public DogProfileDto getDogProfile(@RequestParam Long id){
        return profileService.getDogProfile(id);
    }

    @PostMapping("/profile/dog")
    public ResponseEntity<String> registerDogProfile(@ModelAttribute DogRequestDto dogRequestDto, @AuthenticationPrincipal PrincipalDetails principalDetails){
        profileService.registerDogProfile(principalDetails.getUser(), dogRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body("success register dog");
    }

    @PatchMapping("/profile/dog")
    public ResponseEntity<String> changeDogProfile(@ModelAttribute DogRequestDto dogRequestDto){
        profileService.changeDogProfile(dogRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body("success change dog attr");
    }

    @DeleteMapping("/profile/dog")
    public ResponseEntity<String> deleteDogProfile(@RequestParam Long id){
        profileService.deleteDogProfile(id);
        return ResponseEntity.status(HttpStatus.OK).body("success delete dog");
    }
}
