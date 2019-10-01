package com.cloudnc.interview;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PrimeService {
    private static Logger log = LoggerFactory.getLogger(PrimeService.class);

    private final Server server;

    /**
     * @param port the TCP port to listen on; 0 indicates an ephemeral port
     * @param resourceConfig The {@link ResourceConfig} to load in the server
     */
    private PrimeService(final int port, final ResourceConfig resourceConfig) {
        this.server = new Server(port);
        server.setHandler(jerseyHandler(resourceConfig));
    }

    void start() throws Exception {
        server.start();
    }

    void stop() throws Exception {
        server.stop();
    }

    private void tryStop() {
        try {
            stop();
        }
        catch (final Exception e) {
            log.error("Failed to stop server", e);
        }
    }

    /**
     * @return -1 if not running else the port
     */
    int port() {
        return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    private static Handler jerseyHandler(final ResourceConfig resourceConfig) {
        final ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);
        servletContextHandler.setContextPath("/");
        servletContextHandler.addServlet(jerseyServletHolder(resourceConfig), "/*");
        return servletContextHandler;
    }

    private static ServletHolder jerseyServletHolder(final ResourceConfig resourceConfig) {
        return new ServletHolder(new ServletContainer(resourceConfig));
    }

    private static PrimeService createServer(final int port) {
        final ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(new PrimalityResource(), PrimalityResource.class);
        return new PrimeService(port, resourceConfig);
    }

    static PrimeService createEphemeralServer() {
        return createServer(0);
    }

    public static void main(final String[] args) {
        final PrimeService server = createServer(8080);
        try {
            server.start();
        }
        catch (final Exception e) {
            log.error("Failed to start server.", e);
            server.tryStop();
            return;
        }
        Runtime.getRuntime()
               .addShutdownHook(new Thread(server::tryStop));
        log.info("Shutdown hook installed");

        log.info("Server started");
    }
}
