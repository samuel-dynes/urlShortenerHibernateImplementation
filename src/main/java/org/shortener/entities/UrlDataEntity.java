package org.shortener.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity class representing URL data in the database.
 */
@Entity
@Table(name = "url_shorten")
public class UrlDataEntity {

    /**
     * Unique identifier for the URL data entity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Original URL before shortening
     */
    private String originalUrl;

    /**
     * Shortened URL
     */
    private String shortenedUrl;

    /**
     * Constructs a new `UrlDataEntity` with the specified original URL and shortened URL.
     *
     * @param originalUrl  The original URL before shortening.
     * @param shortenedUrl The shortened URL.
     */
    public UrlDataEntity(final String originalUrl, final String shortenedUrl) {
        this.originalUrl = originalUrl;
        this.shortenedUrl = shortenedUrl;
    }

    /**
     * Default constructor required by JPA.
     */
    public UrlDataEntity() {

    }

    /**
     * Gets the original URL before shortening.
     *
     * @return The original URL.
     */
    public String getOriginalUrl() {
        return originalUrl;
    }

    /**
     * Gets the shortened URL.
     *
     * @return The shortened URL.
     */
    public String getShortenedUrl() {
        return shortenedUrl;
    }
}
