package creative.design.carrotbow.controller;


import creative.design.carrotbow.dto.ListMatchDto;
import creative.design.carrotbow.dto.MatchDto;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.service.MatchService;
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
@RequestMapping("/match")
public class MatchController {

    private final MatchService matchService;


    @GetMapping("/list")
    public ResponseEntity<?> getMatchList(@RequestParam int offset, @AuthenticationPrincipal PrincipalDetails principalDetails){

        List<ListMatchDto> matches = matchService.getMatches(offset, principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("matches", matches);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("")
    public ResponseEntity<?> getMatch(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){

        HashMap<String, Object> result = matchService.getMatch(id, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("")
    public ResponseEntity<?> accept(@RequestParam Long requirementId, @RequestParam Long applicationId, @AuthenticationPrincipal PrincipalDetails principalDetails){
        Long matchId = matchService.makeMatch(requirementId, applicationId, principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("id", matchId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/complete")
    public ResponseEntity<?> complete(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){
        matchService.completeMatch(id, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body("success complete");
    }

    @PutMapping("/cancel")
    public ResponseEntity<?> cancel(@RequestParam Long id, @AuthenticationPrincipal PrincipalDetails principalDetails){
        matchService.cancelMatch(id, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body("success cancel");
    }

}
