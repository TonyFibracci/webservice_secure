package com.cassiomolin.example;

import com.cassiomolin.example.common.api.config.JerseyConfig;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jboss.weld.environment.servlet.Listener;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.ServletException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import static io.undertow.servlet.Servlets.listener;

/**
 * Application entry point.
 *
 * @author cassiomolin
 */
public class Application {

    private static Undertow server;

    private static DeploymentManager deploymentManager;

    private static final int DEFAULT_HTTP_PORT = 8080;
    private static final int DEFAULT_HTTPS_PORT = 443;

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    /**
     * Start server on the port 8080.
     *
     * @param args
     */
    public static void main(final String[] args) {
        startServer(DEFAULT_HTTP_PORT, DEFAULT_HTTPS_PORT);
    }

    /**
     * Start server on the given port.
     *
     * @param port
     */
    public static void startServer(int portHttp, int portHttps) {

        LOGGER.info(String.format("Starting server on port %d", portHttp));

        PathHandler path = Handlers.path();
        
//        SSLContext sslContext = null;
//        try {
//			InputStream is = new FileInputStream("C:\\Users\\Suiteng\\server.crt");
//			 // You could get a resource as a stream instead.
//			
//			 CertificateFactory cf = CertificateFactory.getInstance("X.509");
//			 X509Certificate caCert = (X509Certificate)cf.generateCertificate(is);
//			
//			 TrustManagerFactory tmf = TrustManagerFactory
//			     .getInstance(TrustManagerFactory.getDefaultAlgorithm());
//			 KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
//			 ks.load(null); // You don't need the KeyStore instance to come from a file.
//			 ks.setCertificateEntry("caCert", caCert);
//			
//			 tmf.init(ks);
//			
//			 sslContext = SSLContext.getInstance("TLS");
//			 sslContext.init(null, tmf.getTrustManagers(), null);
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }

        server = Undertow.builder()
                .addHttpListener(portHttp, "localhost")
//                .addHttpsListener(portHttps, "localhost", sslContext)
                .setHandler(path)
                .build();

        server.start();

        LOGGER.info(String.format("Server started on port %d", portHttp));

        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(Application.class.getClassLoader())
                .setContextPath("/")
                .addListeners(listener(Listener.class))
                .setResourceManager(new ClassPathResourceManager(Application.class.getClassLoader()))
                .addServlets(
                        Servlets.servlet("jerseyServlet", ServletContainer.class)
                                .setLoadOnStartup(1)
                                .addInitParam("javax.ws.rs.Application", JerseyConfig.class.getName())
                                .addMapping("/api/*"))
                .setDeploymentName("application.war");

        LOGGER.info("Starting application deployment");

        deploymentManager = Servlets.defaultContainer().addDeployment(servletBuilder);
        deploymentManager.deploy();

        try {
            path.addPrefixPath("/", deploymentManager.start());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Application deployed");
    }

    /**
     * Stop server.
     */
    public static void stopServer() {

        if (server == null) {
            throw new IllegalStateException("Server has not been started yet");
        }

        LOGGER.info("Stopping server");

        deploymentManager.undeploy();
        server.stop();

        LOGGER.info("Server stopped");
    }
}