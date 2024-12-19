package se.berellstudios.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import se.berellstudios.server.entities.TaskEntity;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    TaskEntity findTaskById(int taskId);
}
