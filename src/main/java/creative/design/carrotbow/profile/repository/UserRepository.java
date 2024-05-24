package creative.design.carrotbow.profile.repository;


import creative.design.carrotbow.profile.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public Long save(User user){
        em.persist(user);
        return user.getId();
    }

    public Optional<User> find(Long id){
        return Optional.ofNullable(em.find(User.class, id));
    }

    public Optional<User> findByUsername(String username){

        return em.createQuery("select u from User u where u.username =:username", User.class)
                .setParameter("username", username)
                .getResultList().stream()
                .findFirst();

    }

}
