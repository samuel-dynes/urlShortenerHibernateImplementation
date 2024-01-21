package org.shortener.daos;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.shortener.entities.UrlDataEntity;

/**
 * Data Access Object (DAO) class for performing operations on UrlDataEntity in the database.
 */
public class UrlDataEntityDAO {

    /**
     * Save the given UrlDataEntity to the database.
     *
     * @param urlShorten The UrlDataEntity to be saved.
     */
    public void saveUrl(UrlDataEntity urlShorten, Session session) {
        // Begin a transaction to ensure atomicity of the save operation
        Transaction transaction = session.beginTransaction();

        // Save the UrlDataEntity to the database
        session.persist(urlShorten);
        session.flush();

        // Commit the transaction, persisting the changes
        transaction.commit();
    }

    /**
     * Retrieve a UrlDataEntity based on its shortened URL.
     *
     * @param shortenedUrl The shortened URL to look up.
     * @return The UrlDataEntity associated with the shortened URL.
     * @throws RuntimeException If an error occurs during the retrieval process.
     */
    public UrlDataEntity getUrlFromShortenedUrl(String shortenedUrl, Session session) {
        // Create a parameterized query to retrieve a UrlDataEntity based on its shortened URL
        Query<UrlDataEntity> query = session.createQuery("FROM UrlDataEntity WHERE shortenedUrl = :shortenedUrl",
                                                         UrlDataEntity.class);
        query.setParameter("shortenedUrl", shortenedUrl);

        // Return the unique result (or null if not found)
        return query.uniqueResult();
    }

    /**
     * Retrieve a UrlDataEntity based on its original URL.
     *
     * @param originalUrl The original URL to look up.
     * @return The UrlDataEntity associated with the original URL.
     * @throws RuntimeException If an error occurs during the retrieval process.
     */
    public UrlDataEntity getUrlFromOriginalUrl(String originalUrl, Session session) {
        // Create a parameterized query to retrieve a UrlDataEntity based on its original URL
        Query<UrlDataEntity> query = session.createQuery("FROM UrlDataEntity WHERE originalUrl = :originalUrl",
                                                         UrlDataEntity.class);
        query.setParameter("originalUrl", originalUrl);

        // Return the unique result (or null if not found)
        return query.uniqueResult();
    }
}
