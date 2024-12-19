package se.berellstudios.server.dtos;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class TaskDTO {

    private int id;
    private String messageContent;
    private String status;
    private LocalDateTime deadline;
    private LocalDateTime createdTime;
    private int user_id;

}
