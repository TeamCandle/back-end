package creative.design.carrotbow.controller;


import creative.design.carrotbow.dto.ListMatchDto;
import creative.design.carrotbow.dto.MatchDto;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/application")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/application/list")
    public ResponseEntity<?> getApplicationList(@AuthenticationPrincipal PrincipalDetails principalDetails){

        List<ListMatchDto> applications = applicationService.getApplications(principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("applications", applications);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/application")
    public ResponseEntity<?> getApplication(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        MatchDto application = applicationService.getApplication(id, principalDetails.getName());

        return ResponseEntity.status(HttpStatus.OK).body(application);
    }


    @PostMapping("/application")
    public ResponseEntity<?> applyRequirement(@RequestParam Long requirementId, @AuthenticationPrincipal PrincipalDetails principalDetails){

        Long applicationId = applicationService.apply(requirementId, principalDetails.getName());


        Map<String, Object> result = new HashMap<>();
        result.put("id", applicationId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PutMapping("/application/cancel")
    public ResponseEntity<?> cancelApplication(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){
        applicationService.cancelApplication(id, principalDetails.getName());
        return ResponseEntity.ok().body("success cancel");
    }


}
