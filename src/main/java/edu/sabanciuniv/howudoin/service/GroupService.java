package edu.sabanciuniv.howudoin.service;

import edu.sabanciuniv.howudoin.Utility.JwtUtil;
import edu.sabanciuniv.howudoin.model.Group;
import edu.sabanciuniv.howudoin.model.User;
import edu.sabanciuniv.howudoin.repository.GroupRepository;
import edu.sabanciuniv.howudoin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil; // Inject JwtUtil

    public String createGroup(String token, String groupName, String description, List<String> memberIds) {
        // Ensure creator exists

        // Extract creatorId from token
        String creatorId;
        try {
            creatorId = jwtUtil.extractUserId(token.substring(7)); // Extract userId from the token (Remove "Bearer ")
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token", e);
        }

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        // Ensure all members exist
        for (String memberId : memberIds) {
            if (!userRepository.existsById(memberId)) {
                throw new RuntimeException("User with ID " + memberId + " does not exist");
            }
        }

        // Include the creator in the member list if not already present
        if (!memberIds.contains(creatorId)) {
            memberIds.add(creatorId); // Add creator to members
        }

        // Create the group
        Group group = new Group();
        group.setName(groupName);
        group.setCreatorId(creatorId);
        group.setMemberIds(memberIds);
        group.setAdminIds(List.of(creatorId)); // Set the creator as the admin
        group.setDescription(description);

        // Save the group to the database
        groupRepository.save(group);

        // Update the creator's `groups` field to include the new group
        creator.getGroups().add(group.getId());
        userRepository.save(creator);

        // Update the `groups` field for other members as well
        for (String memberId : memberIds) {
            if (!memberId.equals(creatorId)) { // Skip the creator, already updated
                User member = userRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("Member with ID " + memberId + " not found"));
                member.getGroups().add(group.getId());
                userRepository.save(member);
            }
        }

        return "Group created successfully with ID: " + group.getId();
    }

    /*
    public String createGroup(String token, String groupName, String description, List<String> memberIds) {
        // Ensure creator exists

        // Extract creatorId from token
        String creatorId;
        try {
            creatorId = jwtUtil.extractUserId(token.substring(7)); // Extract userId from the token (Remove "Bearer ")
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token", e);
        }

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        // Ensure all members exist
        for (String memberId : memberIds) {
            if (!userRepository.existsById(memberId)) {
                throw new RuntimeException("User with ID " + memberId + " does not exist");
            }
        }

        // Include the creator in the member list if not already present
        if (!memberIds.contains(creatorId)) {
            memberIds.add(creatorId); // Add creator to members
        }

        // Create the group
        Group group = new Group();
        group.setName(groupName);
        group.setCreatorId(creatorId);
        group.setMemberIds(memberIds);
        group.setAdminIds(List.of(creatorId)); // Set the creator as the admin
        group.setDescription(description);

        groupRepository.save(group);
        return "Group created successfully with ID: " + group.getId();
    }
    */
    public List<Group> getGroupsForUser(String userId) {
        return groupRepository.findAll().stream()
                .filter(group -> group.getMemberIds().contains(userId))
                .toList();
    }

    public Group getGroupDetails(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }
    public String sendGroupInvitation(String token, String groupId, String userId) {
        // Extract admin ID
        String adminId;
        try {
            adminId = jwtUtil.extractUserId(token.substring(7));
            System.out.println("Extracted Admin ID: " + adminId);
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token", e);
        }

        // Fetch group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        System.out.println("Group retrieved: " + group);

        // Ensure `pendingInvitations` is initialized
        if (group.getPendingInvitations() == null) {
            group.setPendingInvitations(new ArrayList<>());
        }

        // Ensure admin and user exist
        if (!group.getAdminIds().contains(adminId)) {
            throw new RuntimeException("Only admins can send group invitations");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user is already invited or a member
        if (group.getMemberIds().contains(userId)) {
            throw new RuntimeException("User is already a group member");
        }
        if (group.getPendingInvitations().contains(userId)) {
            throw new RuntimeException("User is already invited to the group");
        }

        // Add to pending invitations
        group.getPendingInvitations().add(userId);
        groupRepository.save(group);

        return "Group invitation sent successfully.";
    }



    /*
    public String sendGroupInvitation(String token, String groupId, String userId) {
        String adminId;
        try {
            adminId = jwtUtil.extractUserId(token.substring(7));
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token", e);
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getAdminIds().contains(adminId)) {
            throw new RuntimeException("Only admins can send group invitations");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (group.getMemberIds().contains(userId)) {
            throw new RuntimeException("User is already a group member");
        }

        if (group.getPendingInvitations().contains(userId)) {
            throw new RuntimeException("User is already invited to the group");
        }

        group.getPendingInvitations().add(userId);
        groupRepository.save(group);

        return "Group invitation sent successfully.";
    }
    */
    /*
    public String sendGroupInvitation(String adminId, String groupId, String userId) {
        // Ensure group exists
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Ensure the sender is an admin
        if (!group.getAdminIds().contains(adminId)) {
            throw new RuntimeException("Only admins can send group invitations");
        }

        // Ensure the invited user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure the user is not already a member or already invited
        if (group.getMemberIds().contains(userId)) {
            throw new RuntimeException("User is already a group member");
        }
        if (group.getPendingInvitations().contains(userId)) {
            throw new RuntimeException("User is already invited to the group");
        }

        // Add user to the pending invitations
        group.getPendingInvitations().add(userId);
        groupRepository.save(group);

        return "Group invitation sent successfully.";
    }
    */

    public String acceptGroupInvitation(String token, String groupId) {
        String userId;
        try {
            userId = jwtUtil.extractUserId(token);
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token", e);
        }

        // Find the group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if the user has a pending invitation
        if (!group.getPendingInvitations().contains(userId)) {
            throw new RuntimeException("No pending invitation for this user");
        }

        // Add the user to the group's member list and remove from pending invitations
        group.getMemberIds().add(userId);
        group.getPendingInvitations().remove(userId);
        groupRepository.save(group);

        // Update the user's groups list
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getGroups().contains(groupId)) {
            user.getGroups().add(groupId);
        }
        userRepository.save(user);

        return "Group invitation accepted successfully.";
    }



    /*
    public String acceptGroupInvitation(String userId, String groupId) {
        // Ensure group exists
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Ensure the user has a pending invitation
        if (!group.getPendingInvitations().contains(userId)) {
            throw new RuntimeException("No pending invitation for this user");
        }

        // Add the user to the group members and remove from pending invitations
        group.getMemberIds().add(userId);
        group.getPendingInvitations().remove(userId);
        groupRepository.save(group);

        return "Group invitation accepted successfully.";
    }

    */
}
