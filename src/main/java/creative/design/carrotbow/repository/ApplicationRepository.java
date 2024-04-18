package creative.design.carrotbow.repository;

import creative.design.carrotbow.domain.Application;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApplicationRepository {
    private final EntityManager em;

    public Long save(Application application){
        em.persist(application);
        return application.getId();
    }


    public Optional<Application> findById(Long id){
        return Optional.ofNullable(em.find(Application.class, id));
    }


    public Optional<Application> findWithRequirementById(Long id){
        return em.createQuery("select a from Application a" +
                        " join fetch a.requirement r" +
                        " join fetch r.user" +
                        " join fetch r.dog" +
                        " where a.id=:id", Application.class)
                .setParameter("id", id)
                .getResultList()
                .stream().findFirst();
    }

    public List<Application> findListWithRequirementByUsername(String username){
        return em.createQuery("select a from Application a" +
                        " join fetch a.user u" +
                        " join fetch a.requirement r" +
                        " join fetch r.dog" +
                        " where u.username=:username", Application.class)
                .setParameter("username", username)
                .getResultList();
    }


}
