package com.kodnest.pastebin.controller;

import com.kodnest.pastebin.repository.PasteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final PasteRepository pasteRepository;

    public HealthController(PasteRepository pasteRepository) {
        this.pasteRepository = pasteRepository;
    }

    /**
     * Health check endpoint
     * GET /api/healthz
     */
    @GetMapping("/healthz")
    public ResponseEntity<Map<String, Boolean>> health() {
        // simple DB check
        pasteRepository.count();
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
