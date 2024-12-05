package edu.sabanciuniv.howudoin.controller;
import edu.sabanciuniv.howudoin.model.User;
import edu.sabanciuniv.howudoin.repository.UserProjection;
import edu.sabanciuniv.howudoin.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "*")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @PostMapping("/{userId}/sendRequest")
    public ResponseEntity<String> sendFriendRequest(@PathVariable String userId, @RequestParam String friendId) {
        String response = friendService.sendFriendRequest(userId, friendId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/acceptRequest")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable String userId, @RequestParam String friendId) {
        String response = friendService.acceptFriendRequest(userId, friendId);
        return ResponseEntity.ok(response);
    }
    /*
    @GetMapping("/{userId}/list")
    public ResponseEntity<List<User>> getFriends(@PathVariable String userId) {
        List<User> friends = friendService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }
     */

    @GetMapping("/{userId}/list") //filtered get friend
    public ResponseEntity<List<UserProjection>> getFriends(@PathVariable String userId) {
        List<UserProjection> friends = friendService.getFriendsProjection(userId);
        return ResponseEntity.ok(friends);
    }



}
