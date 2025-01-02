package se.berellstudios.server.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private int id;
    private String username;
    private String email;
    private String password;
    private String role;

    //Used to send userdata excluding password to the frontend for security reasons
    public UserResponseDTO(int id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

}
