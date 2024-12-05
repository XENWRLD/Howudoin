// repository/UserRepository.java
package edu.sabanciuniv.howudoin.repository;

import edu.sabanciuniv.howudoin.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);



    @Query(value = "{ '_id': ?0 }", fields = "{ 'friendRequests': 1 }")
    Optional<User> findUserWithFriendRequests(String userId);

    List<UserProjection> findByIdIn(List<String> ids);


}
