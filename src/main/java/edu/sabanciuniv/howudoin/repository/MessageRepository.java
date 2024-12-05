package edu.sabanciuniv.howudoin.repository;

import edu.sabanciuniv.howudoin.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findBySenderAndReceiverOrderByTimestamp(String sender, String receiver);

    List<Message> findByReceiverAndIsReadFalse(String receiver);

    List<Message> findByGroupIdOrderByTimestamp(String groupId);
}

