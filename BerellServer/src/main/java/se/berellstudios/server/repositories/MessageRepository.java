package se.berellstudios.server.repositories;

import se.berellstudios.server.entities.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    MessageEntity findByUserId(int id);

}
