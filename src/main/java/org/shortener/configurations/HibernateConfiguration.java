package org.shortener.configurations;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * HibernateConfiguration class responsible for initializing and providing a Hibernate SessionFactory.
 */
public class HibernateConfiguration {

    /**
     * Static initialization of the Hibernate SessionFactory
     */
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Build and configure the Hibernate SessionFactory.
     *
     * @return Initialized Hibernate SessionFactory.
     * @throws HibernateException If an error occurs during SessionFactory creation.
     */
    private static SessionFactory buildSessionFactory() {
        try {
            // Create a new Configuration and configure it using the hibernate.cfg.xml file
            return new Configuration().configure().buildSessionFactory();
        } catch (HibernateException he) {
            throw new HibernateException(he);
        }
    }

    /**
     * Private constructor to hide implicit constructor and make instantiation impossible.
     */
    private HibernateConfiguration() {
        throw new IllegalStateException("HibernateConfiguration should not be instantiated");
    }

    /**
     * Get the initialized Hibernate SessionFactory.
     *
     * @return Hibernate SessionFactory.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
