package creative.design.carrotbow.matching.domain.repository;

import creative.design.carrotbow.matching.domain.Payment;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class PaymentRepository {

    private final EntityManager em;

    public Long save(Payment payment){
        em.persist(payment);

        return payment.getId();
    }

    public Optional<Payment> findWithMatchById(Long id){
        return em.createQuery("select p from Payment p " +
                        " left join fetch p.match" +
                        " where p.id=:id", Payment.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }

    public Optional<Payment> findWithMatchByMatchId(Long matchId){
        return em.createQuery("select p from Payment p " +
                        " left join fetch p.match m" +
                        " where m.id=:matchId", Payment.class)
                .setParameter("matchId", matchId)
                .getResultStream().findFirst();
    }
}
