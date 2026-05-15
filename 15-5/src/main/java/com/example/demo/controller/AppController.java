package com.example.demo.controller;

import com.example.demo.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping("/")
    public String index(Model model) {
        // Pass all chat history to the view
        model.addAttribute("history", chatMessageRepository.findAll());
        return "index";
    }
}
