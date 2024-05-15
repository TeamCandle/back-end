package creative.design.carrotbow.service;

import creative.design.carrotbow.domain.MatchEntity;
import creative.design.carrotbow.domain.MatchEntityStatus;
import creative.design.carrotbow.domain.Review;
import creative.design.carrotbow.domain.User;
import creative.design.carrotbow.dto.ReviewDto;
import creative.design.carrotbow.dto.requestForm.ReviewRegisterForm;
import creative.design.carrotbow.error.InvalidAccessException;
import creative.design.carrotbow.error.NotFoundException;
import creative.design.carrotbow.repository.ReviewRepository;
import creative.design.carrotbow.security.auth.AuthenticationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MatchService matchService;

    @Transactional
    public Long saveReview(ReviewRegisterForm reviewRegisterForm, AuthenticationUser user){

        Review review = reviewRepository.findByMatchId(reviewRegisterForm.getId()).orElse(null);

        if(review!=null){
            throw new InvalidAccessException("this access is not authorized");
        }

        MatchEntity match = matchService.getMatch(reviewRegisterForm.getId());

        if(!user.getId().equals(match.getRequirement().getUser().getId()) || match.getStatus()!= MatchEntityStatus.COMPLETED){
            throw new InvalidAccessException("this access is not authorized");
        }

        User reviewedUser = match.getApplication().getUser();
        reviewedUser.addReview(reviewRegisterForm.getRating());

        return reviewRepository.save(Review.builder()
                .match(match)
                .reviewedUser(reviewedUser)
                .text(reviewRegisterForm.getText())
                .rating(reviewRegisterForm.getRating())
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public void changeReview(ReviewRegisterForm reviewRegisterForm, AuthenticationUser user){
        Review review = reviewRepository.findById(reviewRegisterForm.getId()).orElseThrow(()->new NotFoundException("can't find review. id:" + reviewRegisterForm.getId()));

        if(!user.getId().equals(review.getMatch().getRequirement().getUser().getId())){
            throw new InvalidAccessException("this access is not authorized");
        }

        User reviewedUser = review.getReviewedUser();
        reviewedUser.subReview(review.getRating());

        review.change(reviewRegisterForm.getText(), reviewRegisterForm.getRating());

        reviewedUser.addReview(review.getRating());
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

    public List<ReviewDto> getReviewsByUser(int offset, AuthenticationUser user){
        List<Review> reviewList  = reviewRepository.findListByUserId(user.getId(), offset);

        ArrayList<ReviewDto> reviews = new ArrayList<>();

        for (Review review : reviewList) {
            reviews.add(ReviewDto.builder()
                            .id(review.getMatch().getId())
                            .text(review.getText())
                            .rating(review.getRating())
                            .createdAt(review.getCreatedAt())
                            .careType(review.getMatch().getRequirement().getCareType())
                            .breed(review.getMatch().getRequirement().getDog().getBreed())
                    .build());
        }

        return reviews;
    }
}
