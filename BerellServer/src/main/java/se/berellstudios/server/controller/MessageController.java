package se.berellstudios.server.controller;

import org.springframework.http.ResponseEntity;
import se.berellstudios.server.dtos.MessageDTO;
import se.berellstudios.server.entities.MessageEntity;
import se.berellstudios.server.entities.UserEntity;
import se.berellstudios.server.repositories.MessageRepository;
import se.berellstudios.server.repositories.UserRepository;
import se.berellstudios.server.utils.AESUtil;
import se.berellstudios.server.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, String>> createMessage(@RequestHeader("Authorization") String token, @RequestBody MessageDTO messageDTO)
            throws Exception {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        UserEntity user = userRepository.findByEmail(username);
        Map<String, String> response = new HashMap<>();

        jwtUtil.jwtCheck(token, user, response);

        String encryptedMessage = aesUtil.encryptMessage(messageDTO.getMessage());

        //Create and save the message entity
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setMessageContent(encryptedMessage);
        messageEntity.setUser(user);
        messageEntity.setCreatedTime(LocalDateTime.now());

        messageRepository.save(messageEntity);

        response.put("message", "Message Created!");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/view")
    public List<String> viewMessages(@RequestHeader("Authorization") String token) throws Exception {
        String jwtToken = token.substring(7);
        if (!jwtUtil.validateToken(jwtToken)) {
            return List.of("Invalid token");
        }

        String userEmail = jwtUtil.extractUsername(jwtToken);
        UserEntity user = userRepository.findByEmail(userEmail);

        //Fetch all time capsules for the user
        List<MessageEntity> messages = user.getMessages();

        //Decrypt each message before returning
        return messages.stream()
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
