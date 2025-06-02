package com.desafio.url.url.services;

import com.desafio.url.url.model.Url;
import com.desafio.url.url.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    public String shortenUrl(String originalUrl) {
        String shortUrl = generateShortUrl();
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(shortUrl);
        url.setExpirationDate(LocalDateTime.now().plusDays(30));
        urlRepository.save(url);
        return shortUrl;
    }

    public Optional<Url> getOriginalUrl(String shortUrl) {
        Optional<Url> urlOptional = urlRepository.findByShortUrl(shortUrl);
        if (urlOptional.isPresent()) {
            Url url = urlOptional.get();
            if (url.getExpirationDate().isAfter(LocalDateTime.now())) {
                return Optional.of(url);
            } else {
                urlRepository.delete(url);
            }
        }
        return Optional.empty();
    }

    private String generateShortUrl() {
        UUID uuid = UUID.randomUUID();
        String base64Encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(uuid.toString().getBytes());
        return base64Encoded.substring(0, 8);
    }
}
