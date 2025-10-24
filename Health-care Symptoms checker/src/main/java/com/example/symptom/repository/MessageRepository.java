package com.example.symptom.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.symptom.model.Message;
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByUserId(String userId);
}
