package edu.sabanciuniv.howudoin.repository;
import org.springframework.beans.factory.annotation.Value;

public interface UserProjection {
    String getId();
    String getName();
    String getLastName();
    String getEmail();
}
