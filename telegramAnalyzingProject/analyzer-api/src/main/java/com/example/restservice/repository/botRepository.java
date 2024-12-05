package com.example.restservice.repository;
import com.example.restservice.Entity.Message;


import java.util.List;

public class botRepository implements accessBotRepository {

    jpaMessageRepository jpaRepository;

    public botRepository(jpaMessageRepository repository) {
        this.jpaRepository = repository;
    }

    @Override
        public List<Message> getAllMessages() {
        return jpaRepository.findAll();
    }


    @Override
    public Message addMessage(Message message) {
        jpaRepository.save(message);
        return message;
    }
}
