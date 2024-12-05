package com.example.restservice.repository;

import com.example.restservice.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface jpaMessageRepository extends JpaRepository<Message, Long> { }
