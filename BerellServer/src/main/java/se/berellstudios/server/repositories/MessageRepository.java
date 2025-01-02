package se.berellstudios.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import se.berellstudios.server.entities.MessageEntity;
import se.berellstudios.server.entities.TaskEntity;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findAllByOrderByDeadlineAsc();

}