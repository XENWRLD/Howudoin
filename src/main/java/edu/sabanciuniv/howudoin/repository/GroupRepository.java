package edu.sabanciuniv.howudoin.repository;

import edu.sabanciuniv.howudoin.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupRepository extends MongoRepository<Group, String> {

}
