package creative.design.carrotbow.matching.service;

import creative.design.carrotbow.matching.domain.MatchEntity;
import creative.design.carrotbow.matching.domain.dto.type.MatchEntityStatus;
import creative.design.carrotbow.matching.domain.Review;
import creative.design.carrotbow.profile.domain.User;
import creative.design.carrotbow.matching.domain.dto.ReviewDto;
import creative.design.carrotbow.matching.domain.dto.requestForm.ReviewRegisterForm;
import creative.design.carrotbow.error.InvalidAccessException;
import creative.design.carrotbow.error.NotFoundException;
import creative.design.carrotbow.matching.domain.repository.ReviewRepository;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j(topic = "ACCESS_LOG")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MatchService matchService;

    @Transactional
    public Long saveReview(ReviewRegisterForm reviewRegisterForm, AuthenticationUser user){

        MatchEntity match = matchService.getMatch(reviewRegisterForm.getMatchId());

        if(!user.getId().equals(match.getRequirement().getUser().getId()) || match.getStatus()!= MatchEntityStatus.COMPLETED){
            throw new InvalidAccessException("this access is not authorized");
        }

        User reviewedUser = match.getApplication().getUser();
        reviewedUser.addReview(reviewRegisterForm.getRating());

        Long reviewId = reviewRepository.save(Review.builder()
                .match(match)
                .reviewedUser(reviewedUser)
                .text(reviewRegisterForm.getText())
                .rating(reviewRegisterForm.getRating())
                .createdAt(LocalDateTime.now())
                .build());

        log.info("리뷰 작성. 리뷰 Id={}", reviewId);

        return reviewId;
    }


    @Transactional
    public void deleteReview(Long id, AuthenticationUser user){
        Review review = reviewRepository.findById(id).orElseThrow(()->new NotFoundException("can't find review. id:" + id));

        if(!user.getId().equals(review.getMatch().getRequirement().getUser().getId())){
            throw new InvalidAccessException("this access is not authorized");
        }

        User reviewedUser = review.getReviewedUser();
        reviewedUser.subReview(review.getRating());

        reviewRepository.delete(review);

        log.info("리뷰 삭제. 리뷰 Id={}", id);
    }


    public HashMap<String, String> getReviewByMatch(Long matchId){
        Review review = reviewRepository.findByMatchId(matchId).orElse(null);

        HashMap<String, String> result = new HashMap<>();

        if(review!=null){
            result.put("id", review.getId().toString());
            result.put("text", review.getText());
            result.put("rating", Float.toString(review.getRating()));
        }else{
            result.put("id", null);
        }

        return result;
    }

    public List<ReviewDto> getReviewsByUser(int offset, Long userId){
        List<Review> reviewList  = reviewRepository.findListByUserId(userId, offset);

        ArrayList<ReviewDto> reviews = new ArrayList<>();

        for (Review review : reviewList) {
            reviews.add(ReviewDto.builder()
                            .id(review.getId())
                            .text(review.getText())
                            .rating(review.getRating())
                            .createdAt(review.getCreatedAt())
                            .careType(review.getMatch().getRequirement().getCareType().getActualName())
                            .breed(review.getMatch().getRequirement().getDog().getBreed())
                    .build());
        }

        return reviews;
    }
}
