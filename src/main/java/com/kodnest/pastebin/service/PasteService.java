package com.kodnest.pastebin.service;

import com.kodnest.pastebin.entity.Paste;
import com.kodnest.pastebin.repository.PasteRepository;
import com.kodnest.pastebin.util.TimeUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasteService {

    private final PasteRepository pasteRepository;

    public PasteService(PasteRepository pasteRepository) {
        this.pasteRepository = pasteRepository;
    }

    public Paste createPaste(String content, Integer ttlSeconds, Integer maxViews) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content must not be empty");
        }

        long now = System.currentTimeMillis();
        String id = UUID.randomUUID().toString().substring(0, 8);

        Paste paste = new Paste();
        paste.setId(id);
        paste.setContent(content);
        paste.setCreatedAtMs(now);
        paste.setViews(0);
        paste.setMaxViews(maxViews);

        if (ttlSeconds != null && ttlSeconds >= 1) {
            paste.setExpiresAtMs(now + ttlSeconds * 1000L);
        }

        return pasteRepository.save(paste);
    }

    @Transactional
    public Paste fetchPaste(String id, HttpServletRequest request) {
        Paste paste = pasteRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Paste not found"));

        long now = TimeUtil.now(request);

        if (paste.getExpiresAtMs() != null && now >= paste.getExpiresAtMs()) {
            throw new IllegalStateException("Paste expired");
        }

        if (paste.getMaxViews() != null &&
                paste.getViews() >= paste.getMaxViews()) {
            throw new IllegalStateException("View limit exceeded");
        }

        paste.setViews(paste.getViews() + 1);

        return pasteRepository.save(paste);
    }

    public Paste getPasteIfAvailable(String id, HttpServletRequest request) {
        Optional<Paste> optionalPaste = pasteRepository.findById(id);
        if (optionalPaste.isEmpty()) {
            return null;
        }

        Paste paste = optionalPaste.get();
        long now = TimeUtil.now(request);

        if (paste.getExpiresAtMs() != null && now >= paste.getExpiresAtMs()) return null;
        if (paste.getMaxViews() != null && paste.getViews() >= paste.getMaxViews()) return null;

        return paste;
    }

    public Integer remainingViews(Paste paste) {
        if (paste.getMaxViews() == null) return null;
        return Math.max(0, paste.getMaxViews() - paste.getViews());
    }

    public String expiresAtIso(Paste paste) {
        if (paste.getExpiresAtMs() == null) return null;
        return Instant.ofEpochMilli(paste.getExpiresAtMs()).toString();
    }
}
