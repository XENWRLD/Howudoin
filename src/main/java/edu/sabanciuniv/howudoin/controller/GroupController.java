package edu.sabanciuniv.howudoin.controller;

import edu.sabanciuniv.howudoin.Utility.JwtUtil;
import edu.sabanciuniv.howudoin.model.Group;
import edu.sabanciuniv.howudoin.service.GroupService;
import edu.sabanciuniv.howudoin.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtUtil jwtUtil; // Add this line

    @PostMapping("/create")
    public ResponseEntity<String> createGroup(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> payload) {
        String name = (String) payload.get("name");
        String description = (String) payload.get("description");
        List<String> memberIds = (List<String>) payload.get("memberIds");

        // Pass the token to the service
        String response = groupService.createGroup(token, name, description, memberIds);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{groupId}/messages")
    public ResponseEntity<?> getGroupMessages(@PathVariable String groupId) {
        return ResponseEntity.ok(messageService.getGroupMessages(groupId));
    }

    @PostMapping("/{groupId}/sendMessage")
    public ResponseEntity<String> sendMessageToGroup(
            @RequestHeader("Authorization") String token,
            @PathVariable String groupId,
            @RequestBody Map<String, String> payload) {
        // Extract the user ID (sender) from the token
        String senderId = jwtUtil.extractUserId(token.startsWith("Bearer ") ? token.substring(7) : token);

        // Extract message content
        String content = payload.get("content");

        // Pass the request to the service
        String response = messageService.sendMessageToGroup(senderId, groupId, content);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/sendInvitation")
    public ResponseEntity<String> sendGroupInvitation(
            @RequestHeader("Authorization") String token,
            @PathVariable String groupId,
            @RequestParam String userId) {
        // Log the received token
        System.out.println("Authorization Header: " + token);

        // Pass the token to the service
        String response = groupService.sendGroupInvitation(token, groupId, userId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{groupId}/acceptInvitation")
    public ResponseEntity<String> acceptGroupInvitation(
            @RequestHeader("Authorization") String token,
            @PathVariable String groupId) {
        // Log the raw token
        System.out.println("Authorization Header: " + token);

        // Extract the token (remove 'Bearer ')
        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        System.out.println("Extracted Token: " + rawToken);

        // Pass to the service layer
        String response = groupService.acceptGroupInvitation(rawToken, groupId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/user/groups")
    public ResponseEntity<List<Group>> getUserGroups(@RequestHeader("Authorization") String token) {
        // Extract user ID from the token
        String userId = jwtUtil.extractUserId(token.startsWith("Bearer ") ? token.substring(7) : token);
        System.out.println("Extracted User ID: " + userId); // Debug log

        // Get groups for the user
        List<Group> userGroups = groupService.getGroupsForUser(userId);
        return ResponseEntity.ok(userGroups);
    }

}
