package creative.design.carrotbow.chat.repository;

import creative.design.carrotbow.chat.domain.Message;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final EntityManager em;

    public Long save(Message message){
        em.persist(message);
        return message.getId();
    }

    public List<Message> findMsgListByRoom(Long roomId){
        return em.createQuery("select m from Message m" +
                        " join fetch m.sender u" +
                        " where m.room.id =:roomId" +
                        " order by m.createdAt asc", Message.class)
                    .setParameter("roomId", roomId)
                    .getResultList();
    }
}
