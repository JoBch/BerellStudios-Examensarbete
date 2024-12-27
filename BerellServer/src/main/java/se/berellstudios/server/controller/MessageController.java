package se.berellstudios.server.controller;

import exceptions.JwtExceptions;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/create")
    public ResponseEntity<?> createMessage(@RequestHeader("Authorization") String token, @RequestBody MessageDTO messageDTO) {
        Map<String, String> response = new HashMap<>();

        try {
            String jwtToken = token.substring(7);
            if (!jwtUtil.validateToken(jwtToken)) {
                throw new Exception("Invalid token");
            }
            String username = jwtUtil.extractUsername(jwtToken);
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
            messageEntity.setCreatedTime(LocalDateTime.now());

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


    @GetMapping("/view")
    public List<String> viewMessages(@RequestHeader("Authorization") String token) throws Exception {
        String jwtToken = token.substring(7);
        if (!jwtUtil.validateToken(jwtToken)) {
            return List.of("Invalid token");
        }

        List<MessageEntity> messageEntities;
        messageEntities = messageRepository.findAllByOrderByDeadlineAsc();

        //Decrypt each message before returning
        return messageEntities.stream()
                .map(msg -> {
                    try {
                        return aesUtil.decryptMessage(msg.getMessageContent());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "Error decrypting message";
                    }
                }).toList();
    }
}
