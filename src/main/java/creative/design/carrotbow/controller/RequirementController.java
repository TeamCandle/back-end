package creative.design.carrotbow.controller;


import creative.design.carrotbow.dto.ListMatchDto;
import creative.design.carrotbow.dto.MatchDto;
import creative.design.carrotbow.dto.RequireRegisterForm;
import creative.design.carrotbow.dto.RequirementCondForm;
import creative.design.carrotbow.error.ErrorResponse;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.RequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/requirement")
public class RequirementController {

    private final RequirementService requirementService;


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

        Long requirementId = requirementService.registerRequirement(requireRegisterForm);

        Map<String, Object> result = new HashMap<>();
        result.put("id", requirementId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/requirement/my-list")
    public ResponseEntity<?> getRequirementListByUser(@AuthenticationPrincipal PrincipalDetails principalDetails){

        List<ListMatchDto> requirements = requirementService.getRequirementsByUser(principalDetails.getUser());

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

        List<ListMatchDto> requirements = requirementService.getRequirementsByLocation(condForm);

        Map<String, Object> result = new HashMap<>();
        result.put("requirements", requirements);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/my-requirement")
    public ResponseEntity<?> getMyRequirement(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){
        HashMap<String, Object> requirement = requirementService.getRequirementWithApplications(id, principalDetails.getName());

        return ResponseEntity.status(HttpStatus.OK).body(requirement);
    }

    @GetMapping("/requirement")
    public ResponseEntity<?> getRequirement(@RequestParam Long id){
        MatchDto requirement = requirementService.getRequirement(id);

        return ResponseEntity.status(HttpStatus.OK).body(requirement);
    }

    @PutMapping("/requirement/cancel")
    public ResponseEntity<?> cancelRequirement(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        requirementService.cancelRequirement(id, principalDetails.getName());

        return ResponseEntity.status(HttpStatus.OK).body("success cancel");
    }


}
