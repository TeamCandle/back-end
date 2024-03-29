package creative.design.carrotbow.repository;


import creative.design.carrotbow.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    @Transactional
    public Long save(User user){
        em.persist(user);
        return user.getId();
    }

    public Optional<User> findByUsername(String username){

        return em.createQuery("select u from User u where u.username =:username", User.class)
                .setParameter("username", username)
                .getResultList().stream()
                .findFirst();

    }
}
