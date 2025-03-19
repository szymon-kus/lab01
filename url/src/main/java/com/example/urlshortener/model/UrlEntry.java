package com.example.urlshortener.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
public class UrlEntry {
    private final String longUrl;
    private final LocalDateTime expiration;
    private final Map<String, UrlEntry> urlStore = new ConcurrentHashMap<>();

}


