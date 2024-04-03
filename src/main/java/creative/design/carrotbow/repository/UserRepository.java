package creative.design.carrotbow.repository;


import creative.design.carrotbow.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public Long save(User user){
        em.persist(user);
        return user.getId();
    }

    public Optional<User> findById(Long id){
        return em.createQuery("select u from User u where u.id =:id", User.class)
                .setParameter("id", id)
                .getResultList().stream()
                .findFirst();
    }

    public Optional<User> findByUsername(String username){

        return em.createQuery("select u from User u where u.username =:username", User.class)
                .setParameter("username", username)
                .getResultList().stream()
                .findFirst();

    }
}
