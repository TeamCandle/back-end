package creative.design.carrotbow.repository;

import creative.design.carrotbow.domain.Payment;
import creative.design.carrotbow.domain.Requirement;
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

    public Optional<Payment> findById(Long id){
        return Optional.ofNullable(em.find(Payment.class, id));
    }

    public Optional<Payment> findWithMatchById(Long id){
        return em.createQuery("select p from Payment p " +
                        " join p.match" +
                        " where p.id=:id", Payment.class)
                .setParameter("id", id)
                .getResultList()
                .stream().findFirst();
    }
}
