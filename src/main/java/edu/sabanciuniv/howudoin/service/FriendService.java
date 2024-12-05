package edu.sabanciuniv.howudoin.service;

import edu.sabanciuniv.howudoin.model.FriendRequest;
import edu.sabanciuniv.howudoin.model.User;
import edu.sabanciuniv.howudoin.repository.UserProjection;
import edu.sabanciuniv.howudoin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendService {

    @Autowired
    private UserRepository userRepository;

    public String sendFriendRequest(String senderId, String receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Debug logs
        System.out.println("Sender ID: " + senderId);
        System.out.println("Receiver ID: " + receiverId);
        System.out.println("Sender's Friends: " + sender.getFriends());
        System.out.println("Receiver's Friend Requests Before Adding: ");
        receiver.getFriendRequests().forEach(request ->
                System.out.println("FriendRequest ID: " + request.getFriendId() + ", Status: " + request.getStatus()));

        if (sender.getFriends().contains(receiverId)) {
            return "Users are already friends.";
        }

        boolean requestExists = receiver.getFriendRequests().stream()
                .anyMatch(request -> request.getFriendId().equals(senderId) && request.getStatus().equals("Pending"));
        if (requestExists) {
            return "Friend request already sent.";
        }

        receiver.addFriendRequest(new FriendRequest(senderId, "Pending"));
        userRepository.save(receiver);

        // Debug logs after save
        User savedReceiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver save verification failed."));
        System.out.println("Receiver's Friend Requests After Save: ");
        savedReceiver.getFriendRequests().forEach(request ->
                System.out.println("FriendRequest ID: " + request.getFriendId() + ", Status: " + request.getStatus()));

        return "Friend request sent successfully.";
    }

    public String acceptFriendRequest(String senderId, String receiverId) {
        // Retrieve sender and receiver from the database
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found. Sender ID: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found. Receiver ID: " + receiverId));

        // Check if the two users are already friends
        if (receiver.getFriends().contains(senderId) && sender.getFriends().contains(receiverId)) {
            return "Users are already friends.";
        }

        // Debug logs for troubleshooting
        System.out.println("Sender Object: " + sender);
        System.out.println("Receiver Object: " + receiver);
        System.out.println("Sender's Friends: " + sender.getFriends());
        System.out.println("Receiver's Friend Requests Before Acceptance: ");
        receiver.getFriendRequests().forEach(request -> {
            System.out.println("FriendRequest -> Friend ID: " + request.getFriendId() + ", Status: " + request.getStatus());
        });

        // Find the friend request in the receiver's list
        FriendRequest friendRequest = receiver.getFriendRequests().stream()
                .filter(request -> request.getFriendId().equals(senderId) && "Pending".equals(request.getStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No pending friend request found from Sender ID: " + senderId));

        // Update the status of the friend request to Accepted
        friendRequest.setStatus("Accepted");

        // Add each user to the other's friend list
        if (!receiver.getFriends().contains(senderId)) {
            receiver.getFriends().add(senderId);
        }
        if (!sender.getFriends().contains(receiverId)) {
            sender.getFriends().add(receiverId);
        }

        // Save both users back to the database
        userRepository.save(receiver);
        userRepository.save(sender);

        // Debug logs after saving
        System.out.println("Receiver's Friend Requests After Acceptance: ");
        receiver.getFriendRequests().forEach(request -> {
            System.out.println("FriendRequest -> Friend ID: " + request.getFriendId() + ", Status: " + request.getStatus());
        });
        System.out.println("Updated Receiver's Friends: " + receiver.getFriends());
        System.out.println("Updated Sender's Friends: " + sender.getFriends());

        return "Friend request accepted successfully.";
    }



    public String rejectFriendRequest(String senderId, String receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        FriendRequest friendRequest = receiver.getFriendRequests().stream()
                .filter(request -> request.getFriendId().equals(senderId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No friend request found."));

        friendRequest.reject();
        userRepository.save(receiver);

        return "Friend request rejected.";
    }

    public List<String> getFriendRequests(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFriendRequests().stream()
                .filter(FriendRequest::isPending)
                .map(FriendRequest::getFriendId)
                .collect(Collectors.toList());
    }

    public String getFriendRequestStatus(String userId, String friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFriendRequests().stream()
                .filter(request -> request.getFriendId().equals(friendId))
                .map(FriendRequest::getStatus)
                .findFirst()
                .orElse("No request found.");
    }

    public List<User> getFriends(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> friendIds = user.getFriends();
        return userRepository.findAllById(friendIds);
    }

    public List<UserProjection> getFriendsProjection(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> friendIds = user.getFriends();
        return userRepository.findByIdIn(friendIds); // Use the corrected repository method
    }

}
