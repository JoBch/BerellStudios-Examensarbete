package se.berellstudios.server.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.berellstudios.server.dtos.UserDTO;
import se.berellstudios.server.entities.UserEntity;
import se.berellstudios.server.repositories.UserRepository;
import se.berellstudios.server.security.Hashing;

@Service
public class UserService {

    private final Hashing hashing;
    @Autowired
    private UserRepository userRepository;

    public UserService() {
        this.hashing = new Hashing();
    }

    public UserEntity findUserById(int id) {
        return userRepository.findById((long) id).orElse(null);
    }

    //Adding user to db
    public void registerUser(UserEntity user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Error!");
        }
        String hashedPassword = hashing.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    //Checking db to validate login
    public boolean loginUser(String email, String password, HttpSession session) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null || !hashing.verifyPassword(password, userEntity.getPassword())) {
            return false;
        }
        UserDTO userDTO = new UserDTO(userEntity.getId(), userEntity.getPassword(), userEntity.getUsername(), userEntity.getRole());
        session.setAttribute("user", userDTO);
        return true;
    }
}
