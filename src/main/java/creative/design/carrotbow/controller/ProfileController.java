package creative.design.carrotbow.controller;

import com.amazonaws.AmazonClientException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import creative.design.carrotbow.dto.DogProfileDto;
import creative.design.carrotbow.dto.DogRequestForm;
import creative.design.carrotbow.dto.UserProfileDto;
import creative.design.carrotbow.exception.DogNotFoundException;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.ProfileService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @ExceptionHandler(DogNotFoundException.class)
    public ResponseEntity handleCustomException(DogNotFoundException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "bad request");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

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
        return ResponseEntity.status(HttpStatus.OK).body("success change");
    }

    @PatchMapping(value = "/profile/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> changeUserImage(@RequestPart("image") MultipartFile image, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        profileService.changeUserImage(principalDetails.getUser(), image);
        return ResponseEntity.status(HttpStatus.OK).body("success change");
    }


    @GetMapping("/profile/dog")
    @ResponseBody
    public DogProfileDto getDogProfile(@RequestParam Long id){
        return profileService.getDogProfile(id);
    }

    @PostMapping("/profile/dog")
    public ResponseEntity<?> registerDogProfile(@Validated @ModelAttribute DogRequestForm dogRequestForm, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principalDetails){

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            return ResponseEntity.badRequest().body(fieldErrors);
        }

        profileService.registerDogProfile(principalDetails.getUser(), dogRequestForm);
        return ResponseEntity.status(HttpStatus.OK).body("success register");
    }

    @PatchMapping("/profile/dog")
    public ResponseEntity<?> changeDogProfile(@Validated @ModelAttribute DogRequestForm dogRequestForm, BindingResult bindingResult){

        if(bindingResult.getTarget()!=null) {
            ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "id", "required");
        }

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            return ResponseEntity.badRequest().body(fieldErrors);
        }

        profileService.changeDogProfile(dogRequestForm);
        return ResponseEntity.status(HttpStatus.OK).body("success change");
    }

    @DeleteMapping("/profile/dog")
    public ResponseEntity<String> deleteDogProfile(@RequestParam Long id){
        profileService.deleteDogProfile(id);
        return ResponseEntity.status(HttpStatus.OK).body("success delete");
    }
}
