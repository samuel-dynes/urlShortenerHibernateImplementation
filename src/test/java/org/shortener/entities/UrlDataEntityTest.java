package org.shortener.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UrlDataEntityTest {

    @Test
    void testConstructorAndGetters() {
        String originalUrl = "http://example.com";
        String shortenedUrl = "abc123";

        UrlDataEntity urlDataEntity = new UrlDataEntity(originalUrl, shortenedUrl);

        assertEquals(originalUrl, urlDataEntity.getOriginalUrl());
        assertEquals(shortenedUrl, urlDataEntity.getShortenedUrl());
    }

    @Test
    void testDefaultConstructor() {
        UrlDataEntity urlDataEntity = new UrlDataEntity();

        assertNull(urlDataEntity.getOriginalUrl());
        assertNull(urlDataEntity.getShortenedUrl());
    }
}
