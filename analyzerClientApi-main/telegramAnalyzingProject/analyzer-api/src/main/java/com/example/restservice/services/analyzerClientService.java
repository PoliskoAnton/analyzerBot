package com.example.restservice.services;

import com.example.restservice.Entity.Post;
import com.example.restservice.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class analyzerClientService {

    ClientRepository repository = null;

    analyzerClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<Post> getAllPosts() {
        return repository.getAllPosts();
    }

    public List<Post> getDailyPosts () {
        return repository.getDailyPosts();
    }

    public Post getLustPost() {
        return repository.getLastPost();
    }

    public void addPost(Post post) {
        repository.addPost(post);
    }




}
