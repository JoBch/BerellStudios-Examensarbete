package se.berellstudios.server.controller;

import exceptions.JwtExceptions;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.berellstudios.server.dtos.MessageDTO;
import se.berellstudios.server.entities.MessageEntity;
import se.berellstudios.server.entities.UserEntity;
import se.berellstudios.server.repositories.MessageRepository;
import se.berellstudios.server.repositories.UserRepository;
import se.berellstudios.server.utils.AESUtil;
import se.berellstudios.server.utils.JWTUtil;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AESUtil aesUtil;

    //Adding new encrypted messages to the DB
    @PostMapping("/create")
    public ResponseEntity<?> createMessage(@RequestHeader("Authorization") String token, @RequestBody MessageDTO messageDTO) {
        Map<String, String> response = new HashMap<>();

        try {
            String jwtToken = token.substring(7);
            if (!jwtUtil.validateToken(jwtToken)) {
                throw new Exception("Invalid token");
            }
            String username = jwtUtil.extractEmail(jwtToken);
            UserEntity user = userRepository.findByEmail(username);

            //Checking the token
            jwtUtil.jwtCheck(token, user);
            //Encrypting message before setting it in the DTO
            String encryptedMessage = aesUtil.encryptMessage(messageDTO.getMessage());

            //Create and save the message entity
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessageContent(encryptedMessage);
            messageEntity.setUser(user);
            messageEntity.setDeadline(messageDTO.getDeadline());
            messageEntity.setCreatedAt(LocalDateTime.now());

            messageRepository.save(messageEntity);

            response.put("message", "Message Created!");
            return ResponseEntity.ok(response);
        } catch (JwtExceptions.InvalidTokenException | JwtExceptions.ExpiredTokenException |
                 JwtExceptions.UserNotFoundException | ParseException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Getting and decrypting messages form the DB
    @GetMapping("/view")
    public ResponseEntity<?> viewMessages(@RequestHeader("Authorization") String token) throws Exception {
        Map<String, String> response = new HashMap<>();

        try {
            String jwtToken = token.substring(7);
            if (!jwtUtil.validateToken(jwtToken)) {
                throw new Exception("Invalid token");
            }

            String userEmail = jwtUtil.extractEmail(jwtToken);
            UserEntity user = userRepository.findByEmail(userEmail);
            jwtUtil.jwtCheck(token, user);

            List<MessageEntity> messageEntities;
            messageEntities = messageRepository.findAllByOrderByDeadlineAsc();

            //Map TaskEntity to TaskDTO
            return getResponseEntity(messageEntities);
        } catch (JwtExceptions.InvalidTokenException | JwtExceptions.ExpiredTokenException |
                 JwtExceptions.UserNotFoundException | ParseException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //Deleting message from DB
    @PostMapping("/delete")
    public ResponseEntity<?> deleteMessage(@RequestHeader("Authorization") String token, @RequestBody MessageDTO messageDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            String jwtToken = token.substring(7);
            if (!jwtUtil.validateToken(jwtToken)) {
                throw new Exception("Invalid token");
            }
            String username = jwtUtil.extractEmail(jwtToken);
            UserEntity user = userRepository.findByEmail(username);

            jwtUtil.jwtCheck(token, user);

            messageRepository.deleteById((long) messageDTO.getId());
            response.put("message", "Message deleted!");
            return ResponseEntity.ok(response);

        } catch (JwtExceptions.InvalidTokenException | JwtExceptions.ExpiredTokenException |
                 JwtExceptions.UserNotFoundException | ParseException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //Edit message
    @PostMapping("/edit")
    @Transactional
    public ResponseEntity<Map<String, String>> editMessage(@RequestHeader("Authorization") String token, @RequestBody MessageDTO messageDTO) {

        Map<String, String> response = new HashMap<>();

        try {
            String jwtToken = token.substring(7);
            if (!jwtUtil.validateToken(jwtToken)) {
                throw new Exception("Invalid token");
            }
            String username = jwtUtil.extractEmail(jwtToken);
            UserEntity user = userRepository.findByEmail(username);

            jwtUtil.jwtCheck(token, user);

            String encryptedMessage = aesUtil.encryptMessage(messageDTO.getMessage());

            //Update and save the message entity
            Optional<MessageEntity> existingMessage = messageRepository.findById((long) messageDTO.getId());
            if (existingMessage.isPresent()) {
                MessageEntity messageEntity = existingMessage.get();
                messageEntity.setMessageContent(encryptedMessage);
                messageEntity.setDeadline(messageDTO.getDeadline());
                messageRepository.save(messageEntity);
            } else {
                throw new JwtExceptions.TaskNotFoundException("Message not found");
            }

            response.put("message", "Message updated successfully!");
            return ResponseEntity.ok(response);
        } catch (JwtExceptions.InvalidTokenException | JwtExceptions.ExpiredTokenException |
                 JwtExceptions.UserNotFoundException | ParseException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private ResponseEntity<?> getResponseEntity(List<MessageEntity> messageEntities) {
        List<MessageDTO> messageDTOs = messageEntities.stream()
                .map(task -> {
                    MessageDTO messageDTO = new MessageDTO();
                    messageDTO.setId(task.getId());
                    try {
                        messageDTO.setMessage(aesUtil.decryptMessage(task.getMessageContent())); //Decrypt message
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    messageDTO.setDeadline(task.getDeadline());
                    messageDTO.setCreatedAt(task.getCreatedAt());
                    messageDTO.setUser_id(task.getUser().getId());
                    return messageDTO;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(messageDTOs);
    }

}
