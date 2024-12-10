package com.example.restservice.repository;

import com.example.restservice.Entity.Message;
import com.example.restservice.repository.accessBotRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryRepository implements accessBotRepository {
    private final List<Message> memory = new ArrayList<>();

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(memory);
    }

    @Override
    public List<Message> getDailyMessages() {
        LocalDate today = LocalDate.now();
        long startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        long endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        return memory.stream()
                .filter(message -> message.getDate() >= startOfDay && message.getDate() < endOfDay)
                .collect(Collectors.toList());
    }

    @Override
    public Message getLustMessage() {
        if (!memory.isEmpty()) {
            return memory.get(memory.size() - 1);
        }
        return null; // Или выброс исключения
    }

    @Override
    public Message addMessage(Message message) {
        memory.add(message);
        return message;
    }


}
