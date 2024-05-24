package creative.design.carrotbow.matching.domain.repository;

import creative.design.carrotbow.matching.domain.Review;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {

    private final EntityManager em;

    @Value("${spring.jpa.page-size}")
    private int pageSize;


    public Long save(Review review){
        em.persist(review);
        return review.getId();
    }

    public Optional<Review> findById(Long id){
        return em.createQuery("select r from Review r" +
                        " join fetch r.match m" +
                        " join fetch m.requirement" +
                        " join fetch r.reviewedUser" +
                        " where r.id=:id", Review.class)
                .setParameter("id",id)
                .getResultList()
                .stream().findFirst();
    }

    public Optional<Review> findByMatchId(Long matchId){
        return em.createQuery("select r from Review r" +
                        " where r.match.id=:matchId", Review.class)
                .setParameter("matchId",matchId)
                .getResultList()
                .stream().findFirst();
    }

    public List<Review> findListByUserId(Long userId, int offset){
        return em.createQuery("select r from Review r" +
                        " join fetch r.match m" +
                        " join fetch m.application a" +
                        " join fetch m.requirement rq" +
                        " join fetch rq.dog" +
                        " where a.user.id=:userId", Review.class)
                .setParameter("userId",userId)
                .setFirstResult(pageSize * (offset-1))
                .setMaxResults(pageSize)
                .getResultList();
    }


    public void delete(Review review){
        em.remove(review);
    }
}
