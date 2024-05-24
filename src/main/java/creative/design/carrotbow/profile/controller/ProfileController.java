package creative.design.carrotbow.profile.controller;

import creative.design.carrotbow.profile.domain.dto.DogProfileDto;
import creative.design.carrotbow.profile.domain.dto.ListDogDto;
import creative.design.carrotbow.profile.domain.dto.DogRegisterForm;
import creative.design.carrotbow.profile.domain.dto.UserProfileDto;
import creative.design.carrotbow.error.ErrorResponse;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;


    @GetMapping({"","/"})
    @ResponseBody
    public String home(){
        return "home";
    }

    @GetMapping("/user/me")
    @ResponseBody
    public UserProfileDto getUserProfile(@AuthenticationPrincipal PrincipalDetails principalDetails){

        return profileService.getUserProfile(principalDetails.getUser().getId());
    }

    @GetMapping("/user")
    @ResponseBody
    public UserProfileDto getUserProfile(@RequestParam Long id){

        return profileService.getUserProfile(id);
    }

    @PatchMapping("/user")
    public ResponseEntity<String> changeUserDescription(@RequestBody(required = false) JSONObject jsonRequest, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String description = jsonRequest != null ? (String)jsonRequest.get("description") : null;

        profileService.changeUserDescription(principalDetails.getUser(), description);
        return ResponseEntity.status(HttpStatus.OK).body("success change");
    }

    @PatchMapping(value = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> changeUserImage(@RequestPart("image") MultipartFile image, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        profileService.changeUserImage(principalDetails.getUser(), image);
        return ResponseEntity.status(HttpStatus.OK).body("success change");
    }


    @GetMapping("/dog")
    @ResponseBody
    public DogProfileDto getDogProfile(@RequestParam Long id){
        return profileService.getDogProfile(id);
    }

    @GetMapping("/dog/list")
    public ResponseEntity<?> getDogList(@AuthenticationPrincipal PrincipalDetails principalDetails){
        List<ListDogDto> dogs = profileService.getDogProfileListByUserId(principalDetails.getUser().getId());

        Map<String, Object> result = new HashMap<>();
        result.put("dogs", dogs);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/dog")
    public ResponseEntity<?> registerDogProfile(@Validated @ModelAttribute DogRegisterForm dogRegisterForm, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principalDetails){

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            List<ErrorResponse> list = new ArrayList<>();

            for (FieldError fieldError : fieldErrors) {
                list.add(ErrorResponse.builder()
                                .message(fieldError.getDefaultMessage())
                                .field(fieldError.getField())
                                .rejectedValue(fieldError.getRejectedValue())
                                .code(fieldError.getCode())
                        .build());
            }

            return ResponseEntity.badRequest().body(list);
        }

        Long dogId = profileService.registerDogProfile(principalDetails.getUser(), dogRegisterForm);

        Map<String, Object> result = new HashMap<>();
        result.put("id", dogId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("/dog")
    public ResponseEntity<?> changeDogProfile(@Validated @ModelAttribute DogRegisterForm dogRegisterForm, BindingResult bindingResult){

        if(bindingResult.getTarget()!=null) {
            ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "id", "required");
        }

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            List<ErrorResponse> list = new ArrayList<>();

            for (FieldError fieldError : fieldErrors) {
                list.add(ErrorResponse.builder()
                        .message(fieldError.getDefaultMessage())
                        .field(fieldError.getField())
                        .rejectedValue(fieldError.getRejectedValue())
                        .code(fieldError.getCode())
                        .build());
            }

            return ResponseEntity.badRequest().body(list);
        }

        profileService.changeDogProfile(dogRegisterForm);
        return ResponseEntity.status(HttpStatus.OK).body("success change");
    }

    @DeleteMapping("/dog")
    public ResponseEntity<String> deleteDogProfile(@RequestParam Long id){
        profileService.deleteDogProfile(id);
        return ResponseEntity.status(HttpStatus.OK).body("success delete");
    }
}
