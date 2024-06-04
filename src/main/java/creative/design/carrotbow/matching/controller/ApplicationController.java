package creative.design.carrotbow.matching.controller;


import com.google.firebase.messaging.FirebaseMessagingException;
import creative.design.carrotbow.matching.domain.dto.ListMatchDto;
import creative.design.carrotbow.matching.domain.dto.MatchDto;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.matching.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j(topic = "ACCESS_LOG")
@RequiredArgsConstructor
@RequestMapping("/application")
public class ApplicationController {

    private final ApplicationService applicationService;


    @GetMapping("/list")
    public ResponseEntity<?> getApplicationList(@RequestParam int offset, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("GET /application/list/me?offset={}",offset);

        List<ListMatchDto> applications = applicationService.getApplications(offset, principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("applications", applications);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("")
    public ResponseEntity<?> getApplication(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("GET /requirement/?id={}", id);

        MatchDto application = applicationService.getApplication(id, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(application);
    }


    @PostMapping("")
    public ResponseEntity<?> applyRequirement(@RequestParam Long requirementId, @AuthenticationPrincipal PrincipalDetails principalDetails) throws FirebaseMessagingException {

        log.info("Post /application/?requirementId={}", requirementId);

        Long applicationId = applicationService.apply(requirementId, principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("id", applicationId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PatchMapping("/cancel")
    public ResponseEntity<?> cancelApplication(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("Patch /application/cancel?id={}", id);

        applicationService.cancelApplication(id, principalDetails.getUser());
        return ResponseEntity.ok().body("success cancel");
    }


}
