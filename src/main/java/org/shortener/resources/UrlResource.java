package org.shortener.resources;

import org.shortener.configurations.HibernateConfiguration;
import org.shortener.daos.UrlDataEntityDAO;
import org.shortener.exceptions.SessionOpenException;
import org.shortener.services.UrlShortenService;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.regex.Pattern;

/**
 * JAX-RS resource class for handling URL shortening and retrieval operations.
 */
@Path("/urls")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
public class UrlResource {

    /**
     * The start of the link to be returned (Should realistically be extracted to a external config file to be set at
     * run, see spring version for this concept in action)
     */
    private static final String BEGINNING_LINK = "http://127.0.0.1:8080/api/urls/";
    /**
     * Service to perform URL shortening and retrieval operations.
     */
    private final UrlShortenService urlShortenService;
    /**
     * Pattern used to validate inputted data is a URL.
     */
    private final Pattern urlPattern = Pattern.compile(
            "^https?://(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_+.~#?&/=]*)"
            + "$");

    /**
     * Constructs a new UrlResource with the provided {@code UrlShortenService}.
     *
     * @param urlShortenService The URL shortening service.
     */
    public UrlResource(final UrlShortenService urlShortenService) {
        this.urlShortenService = urlShortenService;
    }

    /**
     * Default constructor initializing the resource with default dependencies.
     * Used for production and non-testing scenarios.
     */
    public UrlResource() {
        this.urlShortenService = new UrlShortenService(LoggerFactory.getLogger(UrlResource.class),
                                                       HibernateConfiguration.getSessionFactory(),
                                                       new UrlDataEntityDAO());
    }

    /**
     * Endpoint for shortening a URL using a POST request.
     *
     * @param originalUrl The original URL to be shortened.
     * @return Response containing the shortened URL.
     */
    @POST
    public Response shortenUrl(String originalUrl) {
        if (urlPattern.matcher(originalUrl).matches()) {
            try {
                String shortenedUrl = urlShortenService.shortenUrl(originalUrl);
                return Response.ok(BEGINNING_LINK + shortenedUrl).build();
            } catch (SessionOpenException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                               .entity("Error communicating with database")
                               .build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Data to be shortened did not match expectations of a URL.")
                           .build();

        }
    }

    /**
     * Endpoint for retrieving the original URL using a GET request with a shortened URL parameter.
     *
     * @param shortenedUrl The shortened URL to look up.
     * @return Response containing the original URL.
     */
    @GET
    @Path("/{shortenedUrl}")
    public Response getOriginalUrl(@PathParam("shortenedUrl") String shortenedUrl) {
        try {
            String originalUrl = urlShortenService.getOriginalUrl(shortenedUrl);

            if (originalUrl != null) {
                return Response.ok(originalUrl).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Data not found in the database").build();

            }
        } catch (SessionOpenException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error communicating with database")
                           .build();

        }
    }
}
