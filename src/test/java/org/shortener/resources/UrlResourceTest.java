package org.shortener.resources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shortener.exceptions.SessionOpenException;
import org.shortener.services.UrlShortenService;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlResourceTest {

    private final static String ORIGINAL_URL = "http://example.com";
    private final static String SHORTENED_URL = "http://short.url";
    private final static String NOT_A_URL = "not-a-url";
    private static final String BEGINNING_LINK = "http://127.0.0.1:8080/api/urls/";


    @Test
    void testShortenUrlValidData() throws SessionOpenException {
        UrlShortenService shortenService = mock(UrlShortenService.class);
        UrlResource urlResource = new UrlResource(shortenService);
        when(shortenService.shortenUrl(ORIGINAL_URL)).thenReturn(SHORTENED_URL);

        Response response = urlResource.shortenUrl(ORIGINAL_URL);

        verify(shortenService, times(1)).shortenUrl(ORIGINAL_URL);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(BEGINNING_LINK+ SHORTENED_URL, response.getEntity());
    }

    @Test
    void testShortenUrlInvalidData() throws SessionOpenException {
        UrlShortenService shortenService = mock(UrlShortenService.class);
        UrlResource urlResource = new UrlResource(shortenService);

        Response response = urlResource.shortenUrl(NOT_A_URL);

        verify(shortenService, times(0)).shortenUrl(NOT_A_URL);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetOriginalUrl() throws SessionOpenException {
        UrlShortenService shortenService = mock(UrlShortenService.class);
        UrlResource urlResource = new UrlResource(shortenService);
        when(shortenService.getOriginalUrl(SHORTENED_URL)).thenReturn(ORIGINAL_URL);

        Response response = urlResource.getOriginalUrl(SHORTENED_URL);

        verify(shortenService, times(1)).getOriginalUrl(SHORTENED_URL);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(ORIGINAL_URL, response.getEntity());
    }

    @Test
    void testGetOriginalUrlThrowingException() throws SessionOpenException {
        UrlShortenService shortenService = mock(UrlShortenService.class);
        UrlResource urlResource = new UrlResource(shortenService);
        when(shortenService.getOriginalUrl(SHORTENED_URL)).thenThrow(new SessionOpenException("Example exception"));

        Response response = urlResource.getOriginalUrl(SHORTENED_URL);

        verify(shortenService, times(1)).getOriginalUrl(SHORTENED_URL);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetOriginalUrlReturningNull() throws SessionOpenException {
        UrlShortenService shortenService = mock(UrlShortenService.class);
        UrlResource urlResource = new UrlResource(shortenService);
        when(shortenService.getOriginalUrl(SHORTENED_URL)).thenReturn(null);

        Response response = urlResource.getOriginalUrl(SHORTENED_URL);

        verify(shortenService, times(1)).getOriginalUrl(SHORTENED_URL);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

}
