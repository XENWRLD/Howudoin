package edu.sabanciuniv.howudoin.service;

import edu.sabanciuniv.howudoin.model.Group;
import edu.sabanciuniv.howudoin.model.Message;
import edu.sabanciuniv.howudoin.repository.GroupRepository;
import edu.sabanciuniv.howudoin.repository.MessageRepository;
import edu.sabanciuniv.howudoin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(String id) {
        return messageRepository.findById(id);
    }

    public Message createMessage(Message message) {
        return messageRepository.save(message);
    }

    public void deleteMessage(String id) {
        messageRepository.deleteById(id);
    }

    public String sendMessage(String senderId, String receiverId, String content) {

        System.out.println("Validating senderId: " + senderId);
        System.out.println("Validating receiverId: " + receiverId);

        if (!userRepository.existsById(senderId) || !userRepository.existsById(receiverId)) {
            throw new RuntimeException("Sender or receiver not found.");
        }
        if (!userRepository.findById(senderId).get().getFriends().contains(receiverId)) {
            throw new RuntimeException("You can only send messages to friends.");
        }




        Message message = new Message(senderId, receiverId, content, "INDIVIDUAL");
        messageRepository.save(message);
        return "Message sent successfully.";
    }

    public List<Message> getMessages(String senderId, String receiverId) {
        return messageRepository.findBySenderAndReceiverOrderByTimestamp(senderId, receiverId);
    }

    public List<Message> getUnreadMessages(String receiverId) {
        return messageRepository.findByReceiverAndIsReadFalse(receiverId);
    }

    public void markMessagesAsRead(String receiverId, List<String> messageIds) {
        List<Message> messages = messageRepository.findAllById(messageIds);
        for (Message message : messages) {
            if (message.getReceiver().equals(receiverId)) {
                message.setRead(true);
            }
        }
        messageRepository.saveAll(messages);
    }


    public String sendMessageToGroup(String senderId, String groupId, String content) {
        // Ensure group exists
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Ensure sender is a member of the group
        if (!group.getMemberIds().contains(senderId)) {
            throw new RuntimeException("User is not a member of this group");
        }

        // Save the message
        Message message = new Message();
        message.setSender(senderId);
        message.setGroupId(groupId);
        message.setContent(content);
        message.setType("GROUP");
        messageRepository.save(message);

        return "Message sent successfully to group: " + group.getName();
    }

    public List<Message> getGroupMessages(String groupId) {
        return messageRepository.findAll().stream()
                .filter(message -> groupId.equals(message.getGroupId()))
                .toList();
    }


}
