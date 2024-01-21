package org.shortener;

import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.shortener.configurations.ApplicationConfig;

/**
 * Main class to launch the web application using NettyJaxrsServer.
 */
public class WebappMain {

    /**
     * Main method to start the web application.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Create an instance of NettyJaxrsServer
        NettyJaxrsServer server = new NettyJaxrsServer();

        // Set the root resource path for the REST API
        server.setRootResourcePath("/api");

        // Set the port on which the server will listen
        server.setPort(8080);

        // Set the security domain to null (no security domain)
        server.setSecurityDomain(null);

        // Register the ApplicationConfig class with the server's deployment
        server.getDeployment().setApplicationClass(ApplicationConfig.class.getName());

        // Start the server
        server.start();
    }
}
