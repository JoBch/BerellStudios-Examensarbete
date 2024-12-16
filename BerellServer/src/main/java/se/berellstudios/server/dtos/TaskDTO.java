package se.berellstudios.server.dtos;

import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Getter
@Setter
public class TaskDTO {

    private Long id;
    private String messageContent;
    private String status;
    private Date createdTime;
    private Long user_id;

}
