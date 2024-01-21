package org.shortener.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.shortener.daos.UrlDataEntityDAO;
import org.shortener.entities.UrlDataEntity;
import org.shortener.exceptions.SessionOpenException;
import org.slf4j.Logger;

import java.util.Random;

/**
 * Service class for URL shortening and retrieval operations.
 */
public class UrlShortenService {
    /**
     * Characters permitted for generating short URLs (Should realistically be extracted to a external config file to
     * be set at run, see spring version for this concept in action)
     */
    private static final String PERMITTED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * SLF4J logger for logging messages
     */
    private final Logger logger;

    /**
     * Hibernate session factory for database interactions
     */
    private final SessionFactory sessionFactory;

    /**
     * Data Access Object for URL data
     */
    private final UrlDataEntityDAO urlDataEntityDAO;

    /**
     * Random number generator for short URL generation
     */
    private final Random randomGenerator = new Random();

    public UrlShortenService(Logger logger, SessionFactory factory, UrlDataEntityDAO dataEntityDAO) {
        this.logger = logger;
        this.sessionFactory = factory;
        this.urlDataEntityDAO = dataEntityDAO;
    }

    /**
     * Shorten the given original URL.
     *
     * @param originalUrl The original URL to be shortened.
     * @return The shortened URL.
     * @throws SessionOpenException An error occurred communicating with the Database.
     */
    public String shortenUrl(String originalUrl) throws SessionOpenException {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            String shortenedUrl;
            UrlDataEntity alreadyStoredEntity = urlDataEntityDAO.getUrlFromOriginalUrl(originalUrl, session);
            if (alreadyStoredEntity == null) {
                shortenedUrl = generateShortUrl();

                // Naive implementation to ensure shortened url is unique
                while (getOriginalUrl(shortenedUrl) != null) {
                    shortenedUrl = generateShortUrl();
                }

                UrlDataEntity urlDataEntity = new UrlDataEntity(originalUrl, shortenedUrl);

                urlDataEntityDAO.saveUrl(urlDataEntity, sessionFactory.openSession());
                logger.info("Shortened URL created: {}", shortenedUrl);
            } else {
                shortenedUrl = alreadyStoredEntity.getShortenedUrl();
                logger.info("Got existing shortened URL: {}", shortenedUrl);
            }

            transaction.commit();
            return shortenedUrl;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new SessionOpenException("Error shortening URL:" + originalUrl, e);
        }
    }

    /**
     * Retrieve the original URL corresponding to the given shortened URL.
     *
     * @param shortenedUrl The shortened URL to look up.
     * @return The original URL, null if data is not found.
     * @throws SessionOpenException An error occurred communicating with the Database.
     */
    public String getOriginalUrl(String shortenedUrl) throws SessionOpenException {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            UrlDataEntity entity = urlDataEntityDAO.getUrlFromShortenedUrl(shortenedUrl, session);

            if (entity != null) {
                return entity.getOriginalUrl();
            }

            return null;
        } catch (Exception e) {
            throw new SessionOpenException("Error getting original URL from queried string: " + shortenedUrl, e);
        }
    }

    /**
     * Generate a random short URL string of length 8.
     *
     * @return Randomly generated short URL.
     */
    private String generateShortUrl() {
        StringBuilder shortUrlBuilder = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int randomIndex = randomGenerator.nextInt(PERMITTED_CHARS.length());
            shortUrlBuilder.append(PERMITTED_CHARS.charAt(randomIndex));
        }

        return shortUrlBuilder.toString();
    }
}
