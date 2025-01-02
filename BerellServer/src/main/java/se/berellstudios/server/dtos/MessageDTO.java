package se.berellstudios.server.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDTO {

    private int id;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private int user_id;

}
