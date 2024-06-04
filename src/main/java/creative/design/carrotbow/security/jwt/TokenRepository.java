package creative.design.carrotbow.security.jwt;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@Transactional(readOnly = true)
public class TokenRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(RefreshToken refreshToken){
        em.persist(refreshToken);
        return refreshToken.getId();
    }

    public void delete(RefreshToken token){
        em.remove(token);
    }

    public void deleteAll(Long userId){


        em.createQuery("delete from RefreshToken r where r.user.id =:userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }

    public Optional<RefreshToken> findByToken(String token){
        try {
            RefreshToken refreshToken = em.createQuery("SELECT r FROM RefreshToken r WHERE r.token = :token", RefreshToken.class)
                    .setParameter("token", token)
                    .getSingleResult();
            return Optional.of(refreshToken);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
