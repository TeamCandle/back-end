package creative.design.carrotbow.service.msg_queue;

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
                " where m.room.id =:roomId", Message.class)
                .setParameter("roomId", roomId)
                .getResultList();
    }
}
