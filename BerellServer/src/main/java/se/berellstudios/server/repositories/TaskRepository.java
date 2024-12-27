package se.berellstudios.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import se.berellstudios.server.entities.MessageEntity;
import se.berellstudios.server.entities.TaskEntity;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    TaskEntity findTaskById(int taskId);

    List<TaskEntity> findAllByOrderByDeadlineAsc();

    List<TaskEntity> findAllByUserIdOrderByDeadlineAsc(int user_id);

    List<TaskEntity> findTop3ByUserIdOrderByDeadlineAsc(int user_id);

    List<TaskEntity> findTop3ByOrderByDeadlineAsc();
}
