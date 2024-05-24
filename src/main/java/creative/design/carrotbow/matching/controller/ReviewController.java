package creative.design.carrotbow.matching.controller;

import creative.design.carrotbow.matching.domain.dto.ReviewDto;
import creative.design.carrotbow.matching.domain.dto.requestForm.ReviewRegisterForm;
import creative.design.carrotbow.error.ErrorResponse;
import creative.design.carrotbow.security.auth.PrincipalDetails;
import creative.design.carrotbow.matching.service.ReviewService;
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
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<?> writeReview(@Validated @RequestBody ReviewRegisterForm reviewRegisterForm, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principalDetails){
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

        Long reviewId = reviewService.saveReview(reviewRegisterForm, principalDetails.getUser());

        Map<String, Object> result = new HashMap<>();
        result.put("id", reviewId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteReview(@RequestParam Long id, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principalDetails){
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

        reviewService.deleteReview(id, principalDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body("success delete");
    }


    @GetMapping("")
    @ResponseBody
    public HashMap<String,String> getReview(@RequestParam Long matchId){
        return reviewService.getReviewByMatch(matchId);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getReviewList(@RequestParam Long userId, @RequestParam int offset){
        List<ReviewDto> reviews = reviewService.getReviewsByUser(offset, userId);

        Map<String, Object> result = new HashMap<>();
        result.put("reviews", reviews);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
