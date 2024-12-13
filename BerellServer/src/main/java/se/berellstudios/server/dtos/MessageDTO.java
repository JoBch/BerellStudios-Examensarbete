package se.berellstudios.server.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {

    private int id;
    private String message;
    private int user_id;

}
