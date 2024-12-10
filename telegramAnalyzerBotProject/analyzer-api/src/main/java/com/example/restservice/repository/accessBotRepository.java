package com.example.restservice.repository;

import com.example.restservice.Entity.Message;
import java.util.List;

public interface accessBotRepository {
    List<Message> getAllMessages();
    List<Message> getDailyMessages();
    Message getLustMessage();
    Message addMessage(Message message);

}
