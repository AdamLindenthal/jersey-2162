package com.example;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;

public class MyResourceTest extends JerseyTest {

//    private HttpServer server;
    Client c = ClientBuilder.newClient();
//    private WebTarget target = c.target(Main.BASE_URI);;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition responseReceived = lock.newCondition();


    @Override
    public Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        return new ResourceConfig(MyResource.class);
    }

//    @Before
//    public void setUp() throws Exception {
//        // start the server
//        server = Main.startServer();
//        // create the client
//        Client c = ClientBuilder.newClient();
//
//        // uncomment the following line if you want to enable
//        // support for JSON in the client (you also have to uncomment
//        // dependency on jersey-media-json module in pom.xml and Main.startServer())
//        // --
//        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());
//
//        target = c.target(Main.BASE_URI);
//    }

//    @After
//    public void tearDown() throws Exception {
//        server.shutdownNow();
//    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testGetIt() {
        // String responseMsg = target.path("myresource").request().get(String.class);
        listen(10000);
    }

    public void listen(final long time) {
        System.out.println("Listen for " + time + " ms.");
        Future<Response> _response = target().path("rest/sleep/" + time).request().async().get(
                new InvocationCallback<Response>() {
                    public void completed(final Response response) {
                        System.out.println("COMPLETED");
                        lock.lock();
                        try {
                            responseReceived.signalAll();
                        } finally {
                            lock.unlock();
                        }
                    }

                    public void failed(final Throwable throwable) {
                        lock.lock();
                        try {
                            responseReceived.signalAll();
                        } finally {
                            lock.unlock();
                        }
                    }
                }
        );
        lock.lock();
        try {
            System.out.println("Waiting for 5000 ms.");
            if (!responseReceived.await(6000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out!");
                System.out.println("FUTURE: " +  _response + " " + _response.getClass().getName());
                _response.cancel(true);
                listen(3000);
            } else {
                System.out.println("Response received.");
                Response resp = _response.get();
                System.out.println("Received status: " + resp.getStatus());
            }
        } catch (final InterruptedException exception) {
            // Do nothing.
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
