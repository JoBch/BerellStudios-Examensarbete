package se.berellstudios.server.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskDTO {

    private int id;
    private String messageContent;
    private String status;
    @Nullable
    private LocalDateTime deadline;
    private LocalDateTime createdTime;
    private int priority;
    private int user_id;

}
