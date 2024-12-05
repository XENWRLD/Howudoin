package edu.sabanciuniv.howudoin.controller;

import edu.sabanciuniv.howudoin.Utility.JwtUtil;
import edu.sabanciuniv.howudoin.model.Message;
import edu.sabanciuniv.howudoin.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtUtil jwtUtil;



    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestHeader("Authorization") String token, // JWT token in the header
            @RequestBody Map<String, String> payload) { // Receive JSON payload
        try {
            // Extract the receiverId and content from the JSON payload
            String receiverId = payload.get("receiverId");
            String content = payload.get("content");

            // Validate and extract senderId from the token
            String senderId = jwtUtil.extractSenderIdFromToken(token.substring(7));

            // Call the service to process the message
            String response = messageService.sendMessage(senderId, receiverId, content);

            // Return success response
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Return error response if anything goes wrong
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    /*
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestParam String senderId,
            @RequestParam String receiverId,
            @RequestParam String content) {
        try {
            String response = messageService.sendMessage(senderId, receiverId, content);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    */
    @GetMapping("/history")
    public ResponseEntity<List<Message>> getMessageHistory(
            @RequestParam String senderId,
            @RequestParam String receiverId) {
        List<Message> messages = messageService.getMessages(senderId, receiverId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Message>> getUnreadMessages(
            @RequestParam String receiverId) {
        List<Message> unreadMessages = messageService.getUnreadMessages(receiverId);
        return ResponseEntity.ok(unreadMessages);
    }

    @PostMapping("/markRead")
    public ResponseEntity<String> markMessagesAsRead(
            @RequestParam String receiverId,
            @RequestBody List<String> messageIds) {
        messageService.markMessagesAsRead(receiverId, messageIds);
        return ResponseEntity.ok("Messages marked as read.");
    }
}
