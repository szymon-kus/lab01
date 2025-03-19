package com.example.urlshortener.controller;

import com.example.urlshortener.model.UrlEntry;
import com.example.urlshortener.service.UrlShortenerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;
    private final Map<String, UrlEntry> urlStore = new ConcurrentHashMap<>();

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("URL Shortener API is running.");
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestParam String longUrl) {
        try {
            String shortUrl = urlShortenerService.shortenUrl(longUrl);
            return ResponseEntity.ok(shortUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortUrl) {
        UrlEntry entry = urlStore.get(shortUrl);  // Pobieramy obiekt z mapy

        if (entry == null || LocalDateTime.now().isAfter(entry.getExpiration())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(302).header("Location", entry.getLongUrl()).build();
    }

}
