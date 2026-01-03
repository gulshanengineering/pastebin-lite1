package com.kodnest.pastebin.controller;

import com.kodnest.pastebin.entity.Paste;
import com.kodnest.pastebin.service.PasteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Controller
public class PasteController {

    private final PasteService pasteService;

    public PasteController(PasteService pasteService) {
        this.pasteService = pasteService;
    }

    // =================================================
    // CREATE PASTE (API)
    // POST /api/pastes
    // =================================================
    @PostMapping("/api/pastes")
    @ResponseBody
    public ResponseEntity<?> createPaste(@RequestBody Map<String, Object> body,
                                         HttpServletRequest request) {

        String content = (String) body.get("content");
        Integer ttlSeconds = body.get("ttl_seconds") == null
                ? null : ((Number) body.get("ttl_seconds")).intValue();
        Integer maxViews = body.get("max_views") == null
                ? null : ((Number) body.get("max_views")).intValue();

        // -------- Validation --------
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "content must be a non-empty string"));
        }

        if (ttlSeconds != null && ttlSeconds < 1) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "ttl_seconds must be >= 1"));
        }

        if (maxViews != null && maxViews < 1) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "max_views must be >= 1"));
        }

        Paste paste = pasteService.createPaste(content, ttlSeconds, maxViews);

        String baseUrl = request.getScheme() + "://" +
                request.getServerName() +
                (request.getServerPort() == 80 || request.getServerPort() == 443
                        ? "" : ":" + request.getServerPort());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", paste.getId(),
                        "url", baseUrl + "/p/" + paste.getId()
                ));
    }

    // =================================================
    // FETCH PASTE (API)
    // GET /api/pastes/{id}
    // =================================================
    @GetMapping("/api/pastes/{id}")
    @ResponseBody
    public ResponseEntity<?> fetchPaste(@PathVariable String id,
                                        HttpServletRequest request) {

        // Throws IllegalStateException if paste missing/expired/view-limit
        Paste paste = pasteService.fetchPaste(id, request);

        return ResponseEntity.ok(Map.of(
                "content", paste.getContent(),
                "remaining_views", pasteService.remainingViews(paste),
                "expires_at", pasteService.expiresAtIso(paste)
        ));
    }

    // =================================================
    // VIEW PASTE (HTML)
    // GET /p/{id}
    // =================================================
    @GetMapping("/p/{id}")
    public String viewPaste(@PathVariable String id,
                            HttpServletRequest request,
                            Model model) {

        Paste paste = pasteService.getPasteIfAvailable(id, request);

        if (paste == null) {
            // Browser shows 404 page
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // Count this view
        pasteService.fetchPaste(id, request);

        model.addAttribute("content", paste.getContent());
        model.addAttribute("remainingViews", pasteService.remainingViews(paste));
        model.addAttribute("expiresAt", pasteService.expiresAtIso(paste));

        return "paste";
    }
}
