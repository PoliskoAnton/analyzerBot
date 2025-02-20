package com.example.restservice.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String content;

    @NotNull
    @Column(nullable = false)
    private long date;

    protected Post() {}

    public Post(String content, long date) {
        this.content = content;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public long getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "\nContent: " + content + "\nDate: " + date + "\n";
    }
}
