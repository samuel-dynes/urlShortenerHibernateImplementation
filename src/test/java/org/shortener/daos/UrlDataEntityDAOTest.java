package org.shortener.daos;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shortener.entities.UrlDataEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlDataEntityDAOTest {

    @Mock
    private Session mockSession;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private Query<UrlDataEntity> mockQuery;

    @InjectMocks
    private UrlDataEntityDAO urlDataEntityDAO;

    @Test
    void testSaveUrl() {
        UrlDataEntity urlDataEntity = new UrlDataEntity(null, null);
        when(mockSession.beginTransaction()).thenReturn(mockTransaction);

        urlDataEntityDAO.saveUrl(urlDataEntity, mockSession);

        verify(mockSession).persist(urlDataEntity);
        verify(mockSession).flush();
        verify(mockTransaction).commit();
    }

    @Test
    void testGetUrlFromShortenedUrl() {
        String shortenedUrl = "example-shortened-url";
        UrlDataEntity expectedEntity = new UrlDataEntity(null, shortenedUrl);
        setUpQueryMocking(shortenedUrl, expectedEntity);

        UrlDataEntity result = urlDataEntityDAO.getUrlFromShortenedUrl(shortenedUrl, mockSession);

        verify(mockSession).createQuery(anyString(), eq(UrlDataEntity.class));
        verify(mockQuery).setParameter(anyString(), eq(shortenedUrl));
        verify(mockQuery).uniqueResult();
        assertEquals(expectedEntity, result);
    }

    @Test
    void testGetUrlFromOriginalUrl() {
        String originalUrl = "example-original-url";
        UrlDataEntity expectedEntity = new UrlDataEntity(originalUrl, null);
        setUpQueryMocking(originalUrl, expectedEntity);

        UrlDataEntity result = urlDataEntityDAO.getUrlFromOriginalUrl(originalUrl, mockSession);

        verify(mockSession).createQuery(anyString(), eq(UrlDataEntity.class));
        verify(mockQuery).setParameter(anyString(), eq(originalUrl));
        verify(mockQuery).uniqueResult();
        assertEquals(expectedEntity, result);
    }

    private void setUpQueryMocking(String url, UrlDataEntity expectedEntity) {
        when(mockSession.createQuery(anyString(), eq(UrlDataEntity.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), eq(url))).thenReturn(mockQuery);
        when(mockQuery.uniqueResult()).thenReturn(expectedEntity);
    }

}
