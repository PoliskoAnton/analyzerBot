package com.example.restservice.repository.interfaces;

import com.example.restservice.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPostRepository extends JpaRepository<Post, Long> { }
