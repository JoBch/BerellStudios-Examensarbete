package se.berellstudios.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.berellstudios.server.dtos.TaskDTO;
import se.berellstudios.server.entities.TaskEntity;
import se.berellstudios.server.entities.UserEntity;
import se.berellstudios.server.repositories.TaskRepository;
import se.berellstudios.server.repositories.UserRepository;
import se.berellstudios.server.services.UserService;
import se.berellstudios.server.utils.AESUtil;
import se.berellstudios.server.utils.JWTUtil;

import java.util.Map;
@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AESUtil aesUtil;

    @PostMapping("/create")
    public String createTimeCapsule(/*@RequestHeader("Authorization") String token,*/ @RequestBody TaskDTO taskDTO)
            throws Exception {

/*        System.out.println(token);
        //Check if the token is present
        if (token == null || !token.startsWith("Bearer ")) {
            return "No token provided";
        }

        //Extract the token from the header and validate it
        String jwtToken = token.substring(7);
        if (!jwtUtil.validateToken(jwtToken)) {
            return "Invalid token";
        }

        //Check if the token has expired
        if (jwtUtil.isTokenExpired(jwtToken)) {
            return "Token has expired";
        }

        String username = jwtUtil.extractUsername(jwtToken);
        UserEntity user = userRepository.findByEmail(username);
        if (user == null) {
            return "User not found";
        }*/

        String encryptedMessage = aesUtil.encryptMessage(taskDTO.getMessageContent());

        //Create and save the task entity
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setMessageContent(encryptedMessage);
        //taskEntity.setUser(user);

        taskRepository.save(taskEntity);

        return "Task created!";
    }
}


