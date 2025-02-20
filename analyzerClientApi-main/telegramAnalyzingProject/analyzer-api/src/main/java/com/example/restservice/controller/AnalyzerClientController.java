package com.example.restservice.controller;

import com.example.restservice.Entity.Post;
import com.example.restservice.services.analyzerClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class AnalyzerClientController {

    private final analyzerClientService service;

    public AnalyzerClientController(analyzerClientService service) {
        this.service = service;
    }

    @GetMapping("/getAllPosts")
    public List<Post> getAllPost() {
        return service.getAllPosts();
    }

    @GetMapping("/getLastPost")
    public Post getLastPost() {
        return service.getLustPost();
    }

    @GetMapping("/getDailyPosts")
    public List<Post> getDailyPosts() {
        return service.getDailyPosts();
    }

    @PostMapping("/addPost")
    public Post addPost(@RequestBody Post post) {
        service.addPost(post);
        return post;
    }
}
