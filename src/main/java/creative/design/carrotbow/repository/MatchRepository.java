package creative.design.carrotbow.repository;


import creative.design.carrotbow.domain.MatchEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class MatchRepository {

    private final EntityManager em;

    @Value("${spring.jpa.page-size}")
    private int pageSize;

    public Long save(MatchEntity matchEntity){
        em.persist(matchEntity);
        return matchEntity.getId();
    }

    public Optional<MatchEntity> findById(Long id){
        return Optional.ofNullable(em.find(MatchEntity.class, id));
    }

    public Optional<MatchEntity> findWithRequirementById(Long id){
        return em.createQuery("select m from MatchEntity m" +
                        " join fetch m.requirement r" +
                        " where m.id=:id", MatchEntity.class)
                .setParameter("id", id)
                .getResultList()
                .stream().findFirst();
    }

    public Optional<MatchEntity> findWithPaymentById(Long id){
        return em.createQuery("select m from MatchEntity m" +
                        " join fetch m.application a" +
                        " join fetch m.payment p" +
                        " where m.id=:id", MatchEntity.class)
                .setParameter("id", id)
                .getResultList()
                .stream().findFirst();
    }

    public Optional<MatchEntity> findWithFullById(Long id){
        return em.createQuery("select m from MatchEntity m" +
                        " join fetch m.requirement r" +
                        " join fetch r.dog" +
                        " join fetch m.application a" +
                        " join fetch a.user" +
                        " where m.id=:id", MatchEntity.class)
                .setParameter("id", id)
                .getResultList()
                .stream().findFirst();
    }

    public List<MatchEntity> findListWithRequirementByUserId(Long userId, int offset){
        return em.createQuery("select distinct m from MatchEntity m" +
                        " join fetch m.requirement r" +
                        " join fetch m.application a" +
                        " join fetch r.dog" +
                        " where r.user.id=:userId" +
                        " or a.user.id=:userId" +
                        " order by r.startTime asc", MatchEntity.class)
                .setParameter("userId", userId)
                .setFirstResult(pageSize * (offset-1))
                .setMaxResults(pageSize)
                .getResultList();
    }

}
