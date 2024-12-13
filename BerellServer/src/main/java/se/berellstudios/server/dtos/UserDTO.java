package se.berellstudios.server.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private int id;
    private String username;
    private String email;
    private String password;

    public UserDTO(int id, String password, String username) {
        this.password = password;
        this.username = username;
        this.id = id;
    }

}

