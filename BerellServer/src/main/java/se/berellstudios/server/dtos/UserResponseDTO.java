package se.berellstudios.server.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//Need this responseDTO so we dont send the username back to the frontend
public class UserResponseDTO {

    private int id;
    private String username;
    private String email;
    private String password;
    private String role;

    public UserResponseDTO(int id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

}
