package com.kodnest.pastebin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    // Serve the create paste page
    @GetMapping({"/", "/create"})
    public String createPastePage() {
        return "create"; // Thymeleaf template name: create.html
    }
}
