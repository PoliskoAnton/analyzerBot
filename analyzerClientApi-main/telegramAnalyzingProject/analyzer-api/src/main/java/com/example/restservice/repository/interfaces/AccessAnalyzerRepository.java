package com.example.restservice.repository.interfaces;

import com.example.restservice.Entity.Post;
import java.util.List;

public interface AccessAnalyzerRepository {
    List<Post> getAllPosts();
    List<Post> getDailyPosts();
    Post getLastPost();
    void addPost(Post message);
}
