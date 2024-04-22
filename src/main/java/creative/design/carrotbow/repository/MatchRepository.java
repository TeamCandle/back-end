package creative.design.carrotbow.repository;


import creative.design.carrotbow.domain.MatchEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class MatchRepository {

    private final EntityManager em;

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
                        " join fetch r.user" +
                        " where m.id=:id", MatchEntity.class)
                .setParameter("id", id)
                .getResultList()
                .stream().findFirst();
    }

    public Optional<MatchEntity> findWithPaymentById(Long id){
        return em.createQuery("select m from MatchEntity m" +
                        " join fetch m.application a" +
                        " join fetch a.user au" +
                        " join fetch m.payment p" +
                        " join fetch p.user pu" +
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
                        " join fetch a.user au" +
                        " join fetch r.user ru" +
                        " where m.id=:id", MatchEntity.class)
                .setParameter("id", id)
                .getResultList()
                .stream().findFirst();
    }

    public List<MatchEntity> findListWithRequirementByUsername(String username){
        return em.createQuery("select distinct m from MatchEntity m" +
                        " join fetch m.application a" +
                        " join fetch a.user au" +
                        " join fetch m.requirement r" +
                        " join fetch r.dog" +
                        " join fetch r.user ru" +
                        " where ru.username=:username" +
                        " or au.username=:username", MatchEntity.class)
                .setParameter("username", username)
                .getResultList();
    }

}
