package creative.design.carrotbow.profile.repository;

import creative.design.carrotbow.profile.domain.Dog;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DogRepository {

    private final EntityManager em;

    public Long save(Dog dog){
        em.persist(dog);
        return dog.getId();
    }


    public Optional<Dog> findById(Long id){
        return em.createQuery("select d from Dog d" +
                        " where d.id=:id" +
                        " and d.deleted=false", Dog.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }

    public Optional<Dog> findByIdWithUser(Long id){
        return em.createQuery("select d from Dog d" +
                        " join fetch d.owner" +
                        " where d.id=:id" +
                        " and d.deleted=false", Dog.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }

    public List<Dog> findListByUserId(Long userId){
        return em.createQuery("select d from Dog d" +
                        " where d.owner.id=:userId" +
                        " and d.deleted=false ", Dog.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
