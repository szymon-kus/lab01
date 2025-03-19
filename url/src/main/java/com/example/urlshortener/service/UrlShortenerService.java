package com.example.urlshortener.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import com.example.urlshortener.model.UrlEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

@Service
public class UrlShortenerService {

    private final ConcurrentHashMap<String, UrlEntry> urlStore = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();
    private static final long EXPIRATION_TIME_MINUTES = 60;
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");


    private String encodeBase62(long value) {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();

        if (value == 0) return "0"; // Obsługa przypadku, gdy wartość to 0

        while (value > 0) {
            sb.insert(0, chars.charAt((int) (value % 62)));
            value /= 62;
        }

        return sb.toString();
    }

    public String shortenUrl(String longUrl) {
        if (longUrl == null || longUrl.isBlank() || !URL_PATTERN.matcher(longUrl).matches()) {
            throw new IllegalArgumentException("Invalid URL format.");
        }

        long id = counter.incrementAndGet();
        String shortUrl = encodeBase62(id);
        urlStore.put(shortUrl, new UrlEntry(longUrl, LocalDateTime.now().plusMinutes(EXPIRATION_TIME_MINUTES)));

        return "http://localhost:8083/api/" + shortUrl;
    }

    public String getLongUrl(String shortUrl) {
        UrlEntry entry = urlStore.get(shortUrl);
        if (entry == null || LocalDateTime.now().isAfter(entry.getExpiration())) {
            return null;
        }
        return entry.getLongUrl();  // 🔹 Teraz działa poprawnie!
    }
}
