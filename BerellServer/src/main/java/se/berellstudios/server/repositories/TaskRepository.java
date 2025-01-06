package se.berellstudios.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.berellstudios.server.entities.TaskEntity;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findAllByOrderByDeadlineAsc();

    List<TaskEntity> findAllByUserIdOrderByDeadlineAsc(int user_id);

    List<TaskEntity> findTop3ByUserIdOrderByDeadlineAsc(int user_id);

    List<TaskEntity> findTop3ByOrderByDeadlineAsc();

    //Custom query to set a new status on task by id
    @Modifying
    @Query("UPDATE tasks t SET t.status = :status WHERE t.id = :id")
    void updateStatusById(@Param("id") int id, @Param("status") String status);

}
