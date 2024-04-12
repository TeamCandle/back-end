package creative.design.carrotbow.controller;


import creative.design.carrotbow.dto.ListMatchDto;
import creative.design.carrotbow.dto.MatchDto;
import creative.design.carrotbow.dto.RequireRegisterForm;
import creative.design.carrotbow.dto.RequirementCondForm;
import creative.design.carrotbow.error.ErrorResponse;
import creative.design.carrotbow.error.WrongApplicationException;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;


    @ExceptionHandler(WrongApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(WrongApplicationException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Wrong Application");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    //Requirement

    @PostMapping("/requirement")
    public ResponseEntity<?> registerRequirement(@Validated @RequestBody RequireRegisterForm requireRegisterForm, BindingResult bindingResult){

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

        Long requirementId = matchService.registerRequirement(requireRegisterForm);

        Map<String, Object> result = new HashMap<>();
        result.put("id", requirementId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/requirement/my-list")
    public ResponseEntity<?> getRequirementListByUser(@AuthenticationPrincipal PrincipalDetails principalDetails){

        List<ListMatchDto> requirements = matchService.getRequirementsByUser(principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("requirements", requirements);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping(value = "/requirement/list")
    public ResponseEntity<?> getRequirementListByLocation(@RequestBody RequirementCondForm condForm, BindingResult bindingResult){

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

        List<ListMatchDto> requirements = matchService.getRequirementsByLocation(condForm);

        Map<String, Object> result = new HashMap<>();
        result.put("requirements", requirements);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/requirement")
    public ResponseEntity<?> getRequirement(@RequestParam Long id){
        HashMap<String, Object> requirement = matchService.getRequirement(id);

        return ResponseEntity.status(HttpStatus.OK).body(requirement);
    }

    @PutMapping("/requirement/cancel")
    public ResponseEntity<?> cancelRequirement(@RequestParam Long id){

        matchService.cancelRequirement(id);

        return ResponseEntity.status(HttpStatus.OK).body("success cancel");
    }



    //Application

    @GetMapping("/application/list")
    public ResponseEntity<?> getApplicationList(@AuthenticationPrincipal PrincipalDetails principalDetails){

        List<ListMatchDto> applications = matchService.getApplications(principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("applications", applications);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/application")
    public ResponseEntity<?> getApplication(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        MatchDto application = matchService.getApplication(id);

        return ResponseEntity.status(HttpStatus.OK).body(application);
    }


    @PostMapping("/application")
    public ResponseEntity<?> applyRequirement(@RequestParam Long requirementId, @AuthenticationPrincipal PrincipalDetails principalDetails){

        Long applicationId = matchService.apply(requirementId, principalDetails.getName());


        Map<String, Object> result = new HashMap<>();
        result.put("id", applicationId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/application/reject")
    public ResponseEntity<?> rejectApplication(@RequestParam Long id){
        matchService.rejectApplication(id);
        return ResponseEntity.ok().body("success reject");
    }


    @PutMapping("/application/cancel")
    public ResponseEntity<?> cancelApplication(@RequestParam Long id){
        matchService.cancelApplication(id);
        return ResponseEntity.ok().body("success cancel");
    }



    //Match

    @GetMapping("/match/list")
    public ResponseEntity<?> getMatchList(@AuthenticationPrincipal PrincipalDetails principalDetails){

        List<ListMatchDto> matchs = matchService.getMatchs(principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("matchs", matchs);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/match")
    public ResponseEntity<?> getMatch(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        MatchDto application = matchService.getMatch(id, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(application);
    }


    @PostMapping("/match")
    public ResponseEntity<?> accept(@RequestParam Long requirementId, @RequestParam Long applicationId){
        Long matchId = matchService.makeMatch(requirementId, applicationId);

        Map<String, Object> result = new HashMap<>();
        result.put("id", matchId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
