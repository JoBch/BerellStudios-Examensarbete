package se.berellstudios.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import se.berellstudios.server.entities.MessageEntity;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    MessageEntity findByUserId(int id);

    List<MessageEntity> findAllByOrderByDeadlineAsc();
}

