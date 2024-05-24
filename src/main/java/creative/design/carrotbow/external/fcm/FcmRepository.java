package creative.design.carrotbow.external.fcm;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FcmRepository {

    private final EntityManager em;


    public Long save(FcmToken fcmToken){
        em.persist(fcmToken);
        return fcmToken.getId();
    }


    public Optional<FcmToken> findByUser(Long userId){
        return em.createQuery("select t from FcmToken t where t.user.id=:userId", FcmToken.class)
                .setParameter("userId", userId)
                .getResultList().stream().findFirst();
    }
}
