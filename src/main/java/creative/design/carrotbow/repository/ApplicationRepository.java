package creative.design.carrotbow.repository;

import creative.design.carrotbow.domain.Application;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApplicationRepository {
    private final EntityManager em;
    @Value("${spring.jpa.page-size}")
    private int pageSize;

    public Long save(Application application){
        em.persist(application);
        return application.getId();
    }


    public Optional<Application> find(Long id){
        return Optional.ofNullable(em.find(Application.class, id));
    }


    public Optional<Application> findWithRequirementById(Long id){
        return em.createQuery("select a from Application a" +
                        " join fetch a.requirement r" +
                        " join fetch r.dog" +
                        " where a.id=:id", Application.class)
                .setParameter("id", id)
                .getResultList()
                .stream().findFirst();
    }

    public List<Application> findListWithRequirementByUserId(Long userId, int offset){
        return em.createQuery("select a from Application a" +
                        " join fetch a.requirement r" +
                        " join fetch r.dog" +
                        " where a.user.id=:userId" +
                        " order by r.startTime asc", Application.class)
                .setParameter("userId", userId)
                .setFirstResult(pageSize * (offset-1))
                .setMaxResults(pageSize)
                .getResultList();
    }


}
