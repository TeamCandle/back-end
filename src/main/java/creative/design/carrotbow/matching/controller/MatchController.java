package creative.design.carrotbow.matching.controller;


import com.google.firebase.messaging.FirebaseMessagingException;
import creative.design.carrotbow.matching.domain.dto.ListMatchDto;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.matching.service.MatchService;
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
@RequestMapping("/match")
public class MatchController {

    private final MatchService matchService;



    @GetMapping("/list")
    public ResponseEntity<?> getMatchList(@RequestParam int offset, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("GET /match/list?offset={}", offset);

        List<ListMatchDto> matches = matchService.getMatches(offset, principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("matches", matches);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/upcoming")
    @ResponseBody
    public ResponseEntity<?> getUpcomingMatch(@AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("GET /match/upcoming");

        ListMatchDto result = matchService.getUpcomingMatch(principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("")
    public ResponseEntity<?> getMatch(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("GET /match?id={}", id);

        HashMap<String, Object> result = matchService.getMatch(id, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("")
    public ResponseEntity<?> accept(@RequestParam Long requirementId, @RequestParam Long applicationId, @AuthenticationPrincipal PrincipalDetails principalDetails) throws FirebaseMessagingException {

        log.info("POST /match?requirementId={}&applicationId={}", requirementId, applicationId);

        Long matchId = matchService.makeMatch(requirementId, applicationId, principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("id", matchId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("/complete")
    public ResponseEntity<?> complete(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("PATCH /match/complete?id={}", id);

        matchService.completeMatch(id, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body("success complete");
    }

    @PatchMapping("/cancel")
    public ResponseEntity<?> cancel(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        log.info("PATCH /match/cancel?id={}", id);

        matchService.cancelMatch(id, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body("success cancel");
    }

}
