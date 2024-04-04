package creative.design.carrotbow.repository;

import creative.design.carrotbow.domain.Dog;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DogRepository {

    private final EntityManager em;

    public Long save(Dog dog){
        em.persist(dog);
        return dog.getId();
    }

    public void deleteById(Long id){
        em.createQuery("delete from Dog d where d.id=:id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public Optional<Dog> findById(Long id){
       return Optional.ofNullable(em.find(Dog.class, id));
    }

    public Optional<Dog> findByIdWithUser(Long id){
        return em.createQuery("select d from Dog d join fetch d.owner where d.id=:id", Dog.class)
                .setParameter("id", id)
                .getResultList().stream().findFirst();
    }
}
