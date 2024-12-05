package com.example.restservice.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private final String content;
    @NotNull
    private final int date;


    public Message(String content, int date) {
        this.content = content;
        this.date = date;
    }

    public String getContent() {
        return content;
    }
    public int getDate() {
        return date;
    }

}
