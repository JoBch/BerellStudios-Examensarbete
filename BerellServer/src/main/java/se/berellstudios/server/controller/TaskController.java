package se.berellstudios.server.controller;

import exceptions.JwtExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.berellstudios.server.dtos.TaskDTO;
import se.berellstudios.server.entities.TaskEntity;
import se.berellstudios.server.entities.UserEntity;
import se.berellstudios.server.repositories.TaskRepository;
import se.berellstudios.server.repositories.UserRepository;
import se.berellstudios.server.utils.AESUtil;
import se.berellstudios.server.utils.JWTUtil;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AESUtil aesUtil;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createTasks(@RequestHeader("Authorization") String token, @RequestBody TaskDTO taskDTO) {

        Map<String, String> response = new HashMap<>();

        try {
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            UserEntity user = userRepository.findByEmail(username);

            //Checking the token
            jwtUtil.jwtCheck(token, user);

            String encryptedMessage = aesUtil.encryptMessage(taskDTO.getMessageContent());

            //Create and save the task entity
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setMessageContent(encryptedMessage);
            taskEntity.setUser(user);
            taskEntity.setStatus(taskDTO.getStatus());
            taskEntity.setDeadline(taskDTO.getDeadline());
            taskEntity.setCreatedTime(LocalDateTime.now());

            taskRepository.save(taskEntity);

            response.put("message", "Task Created!");
            return ResponseEntity.ok(response);
        } catch (JwtExceptions.InvalidTokenException | JwtExceptions.ExpiredTokenException |
                 JwtExceptions.UserNotFoundException | ParseException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/view")
    public List<TaskDTO> viewTasks(@RequestHeader("Authorization") String token) throws Exception {
        String jwtToken = token.substring(7);
        if (!jwtUtil.validateToken(jwtToken)) {
            throw new Exception("Invalid token");
        }
        //Checking for role, admins should see all tasks, users only the ones assigned to them.
        List<TaskEntity> taskEntities;
        if (Objects.equals(jwtUtil.extractRole(jwtToken), "user")) {
            String userEmail = jwtUtil.extractUsername(jwtToken);
            UserEntity user = userRepository.findByEmail(userEmail);
            //Fetch all tasks for the user
            taskEntities = user.getTasks();
        } else {//Fetch all tasks
            taskEntities = taskRepository.findAll();
        }

        //Map TaskEntity to TaskDTO
        return taskEntities.stream()
                .map(task -> {
                    TaskDTO taskDTO = new TaskDTO();
                    taskDTO.setId(task.getId());
                    try {
                        taskDTO.setMessageContent(aesUtil.decryptMessage(task.getMessageContent()));  // Decrypt message
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    taskDTO.setStatus(task.getStatus());
                    taskDTO.setDeadline(task.getDeadline());
                    taskDTO.setCreatedTime(task.getCreatedTime());
                    taskDTO.setUser_id(task.getUser().getId());
                    return taskDTO;
                }).collect(Collectors.toList());
    }

}


