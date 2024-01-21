package org.shortener.services;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shortener.daos.UrlDataEntityDAO;
import org.shortener.entities.UrlDataEntity;
import org.shortener.exceptions.SessionOpenException;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlShortenServiceTest {

    private final static String ORIGINAL_URL = "http://example.com";
    private final static String SHORTENED_CHARS = "abc12345";

    @Mock
    private Logger mockLogger;

    @Mock
    private SessionFactory mockSessionFactory;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private Session mockSession;

    @Mock
    private UrlDataEntityDAO mockUrlDataEntityDAO;

    @InjectMocks
    private UrlShortenService urlShortenService;

    @Test
    void testShortenUrlNewUrlSuccess() throws SessionOpenException {
        when(mockSessionFactory.openSession()).thenReturn(mockSession);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        doNothing().when(mockTransaction).commit();
        when(mockUrlDataEntityDAO.getUrlFromOriginalUrl(ORIGINAL_URL, mockSession)).thenReturn(null);
        doNothing().when(mockUrlDataEntityDAO).saveUrl(any(UrlDataEntity.class), any(Session.class));

        String result = urlShortenService.shortenUrl(ORIGINAL_URL);

        verify(mockSession, times(2)).beginTransaction();
        verify(mockUrlDataEntityDAO).getUrlFromOriginalUrl(eq(ORIGINAL_URL), any(Session.class));
        verify(mockUrlDataEntityDAO).saveUrl(any(UrlDataEntity.class), any(Session.class));
        assertEquals(8, result.length());
    }

    @Test
    void testShortenUrlExistingUrlSuccess() throws SessionOpenException {
        UrlDataEntity existingUrlDataEntity = new UrlDataEntity(ORIGINAL_URL, SHORTENED_CHARS);
        when(mockSessionFactory.openSession()).thenReturn(mockSession);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        doNothing().when(mockTransaction).commit();
        when(mockUrlDataEntityDAO.getUrlFromOriginalUrl(ORIGINAL_URL, mockSession)).thenReturn(existingUrlDataEntity);

        String result = urlShortenService.shortenUrl(ORIGINAL_URL);

        verify(mockUrlDataEntityDAO).getUrlFromOriginalUrl(ORIGINAL_URL, mockSession);
        verify(mockLogger).info("Got existing shortened URL: {}", SHORTENED_CHARS);
        assertEquals(SHORTENED_CHARS, result);
    }

    @Test
    void testShortenUrlNewUrlThrow() {
        when(mockSessionFactory.openSession()).thenThrow(new HibernateException("Testing"));

        assertThrows(SessionOpenException.class, () -> urlShortenService.shortenUrl(ORIGINAL_URL));

        verifyNoInteractions(mockUrlDataEntityDAO);
    }

    @Test
    void testGetOriginalUrlSuccess() throws SessionOpenException {
        UrlDataEntity urlDataEntity = new UrlDataEntity(ORIGINAL_URL, SHORTENED_CHARS);
        when(mockSessionFactory.openSession()).thenReturn(mockSession);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);
        when(mockUrlDataEntityDAO.getUrlFromShortenedUrl(SHORTENED_CHARS, mockSession)).thenReturn(urlDataEntity);

        String result = urlShortenService.getOriginalUrl(SHORTENED_CHARS);

        verify(mockUrlDataEntityDAO).getUrlFromShortenedUrl(SHORTENED_CHARS, mockSession);
        assertEquals(ORIGINAL_URL, result);
    }

    @Test
    void testGetOriginalUrlThrows(){
        when(mockSessionFactory.openSession()).thenThrow(new HibernateException("Testing"));

        assertThrows(SessionOpenException.class, () -> urlShortenService.getOriginalUrl(SHORTENED_CHARS));

        verifyNoInteractions(mockUrlDataEntityDAO);
    }
}
