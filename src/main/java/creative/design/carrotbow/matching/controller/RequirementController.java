package creative.design.carrotbow.matching.controller;


import creative.design.carrotbow.matching.domain.dto.ListMatchDto;
import creative.design.carrotbow.matching.domain.dto.MatchDto;
import creative.design.carrotbow.matching.domain.dto.requestForm.RequireRegisterForm;
import creative.design.carrotbow.matching.domain.dto.requestForm.RequirementCondForm;
import creative.design.carrotbow.error.ErrorResponse;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.matching.service.RequirementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
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
@Slf4j(topic = "ACCESS_LOG")
@RequiredArgsConstructor
@RequestMapping("/requirement")
public class RequirementController {

    private final RequirementService requirementService;

    @PostMapping("")
    public ResponseEntity<?> registerRequirement(@Validated @RequestBody RequireRegisterForm requireRegisterForm, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("POST /requirement dog Id={}", requireRegisterForm.getDogId());

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

        Long requirementId = requirementService.registerRequirement(requireRegisterForm, principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("id", requirementId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyRequirement(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("GET /requirement/me?id={}", id);

        HashMap<String, Object> requirement = requirementService.getRequirementWithApplications(id, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(requirement);
    }

    @GetMapping("")
    public ResponseEntity<?> getRequirement(@RequestParam Long id){

        log.info("GET /requirement/id={}", id);

        MatchDto requirement = requirementService.getRequirement(id);

        return ResponseEntity.status(HttpStatus.OK).body(requirement);
    }

    @GetMapping("/list/me")
    public ResponseEntity<?> getRequirementListByUser(@RequestParam int offset, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("GET /requirement/list/me?offset={}",offset);

        List<ListMatchDto> requirements = requirementService.getRequirementsByUser(offset, principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("requirements", requirements);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/list")
    public ResponseEntity<?> getRequirementListByLocation(@RequestParam int offset, @RequestBody RequirementCondForm condForm, BindingResult bindingResult){

        log.info("POST /requirement/list?offset={}",offset);

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

        List<ListMatchDto> requirements = requirementService.getRequirementsByLocation(condForm, offset);

        Map<String, Object> result = new HashMap<>();
        result.put("requirements", requirements);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("/cancel")
    public ResponseEntity<?> cancelRequirement(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("PATCH /requirement/cancel?id={}",id);

        requirementService.cancelRequirement(id, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body("success cancel");
    }


}
