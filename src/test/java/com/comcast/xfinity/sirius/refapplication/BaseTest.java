package com.comcast.xfinity.sirius.refapplication;

import com.comcast.xfinity.sirius.refapplication.store.Repository;
import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.MediaTypes;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;


@RunWith(JUnit4.class)
public class BaseTest  {
    private static SelectorThread selectorThread;
    private static Client client;
    private static WebResource webResource;
    private static String CUSTOM_WRITE_AHEAD_LOG = "uberStore/";
    private static int CUSTOM_SERVER_PORT = 9998;
    private static int CUSTOM_SIRIUS_PORT = 42289;
    private static URI CUSTOM_BASE_URI = UriBuilder.fromUri("http://localhost/storage").port(CUSTOM_SERVER_PORT).build();

    @BeforeClass
    public static void oneTimeSetUp()throws Exception {
        client = Client.create();
        webResource = client.resource(CUSTOM_BASE_URI);
        selectorThread = StartServer.startServer(CUSTOM_SIRIUS_PORT, CUSTOM_WRITE_AHEAD_LOG, CUSTOM_BASE_URI);
        System.out.println("@BeforeClass - oneTimeSetUp");
        Thread.sleep(2000L);
    }

    @Test
    public void testApplicationWADL() {
        ClientResponse response = webResource.path("application.wadl").accept(MediaTypes.WADL).get(ClientResponse.class);
        String serviceWadl = response.getEntity(String.class);
        assertTrue("A WADL for the application is available", response.getStatus() == 200);
    }

    @Test
    public void testRepositoryIsAvailable() {
        Repository repository = webResource.path("repository").accept(MediaType.APPLICATION_XML).get(Repository.class);
        assertNotNull(repository);
    }

    @Test
    public void testCreateContainer() {
        WebResource childWebResource = webResource.path("repository").path("Talking Smack");
        ClientResponse response = childWebResource.put(ClientResponse.class);
        assertEquals(Response.Status.CREATED, response.getResponseStatus());
    }

    @Test
    public void testCreateContainerWithData() {
        WebResource childWebResource = webResource.path("repository").path("aesopfables");
        ClientResponse response = childWebResource.put(ClientResponse.class);
        assertEquals(Response.Status.CREATED, response.getResponseStatus());

        response = childWebResource.path("Androcles").type(MediaType.TEXT_PLAIN).put(ClientResponse.class, "Gratitude is the sign of noble souls");
        assertEquals(Response.Status.CREATED, response.getResponseStatus());

        response = childWebResource.path("The Ant and the Chrysalis").type(MediaType.TEXT_PLAIN).put(ClientResponse.class, "Appearances are deceptive");
        assertEquals(Response.Status.CREATED, response.getResponseStatus());
    }


    @Test
    public void testUpdatingData() {
        String originalValue1;
        String changedValue1;
        String originalValue2;
        String changedValue2;

        WebResource childWebResource = webResource.path("repository").path("Listings");
        ClientResponse response = childWebResource.put(ClientResponse.class);
        assertEquals(Response.Status.CREATED, response.getResponseStatus());

        response = childWebResource.path("Breaking Bad").accept(MediaType.APPLICATION_XML).put(ClientResponse.class, "M-W-F 2:00 PM");
        originalValue1 = childWebResource.get(String.class);
        assertEquals(Response.Status.CREATED, response.getResponseStatus());

        response = childWebResource.path("Grimm").accept(MediaType.APPLICATION_XML).put(ClientResponse.class, "M-W-F 2:00 PM");
        originalValue2 = childWebResource.get(String.class);
        assertEquals(Response.Status.CREATED, response.getResponseStatus());

        response = childWebResource.path("Breaking Bad").accept(MediaType.APPLICATION_XML).put(ClientResponse.class, "M-W-F 5:00 PM");
        changedValue1 = childWebResource.get(String.class);
        assertEquals(Response.Status.CREATED, response.getResponseStatus());
        assertNotEquals(originalValue1, changedValue1);

        response = childWebResource.path("Grimm").accept(MediaType.APPLICATION_XML).put(ClientResponse.class, "T-TH 8:00 PM");
        changedValue2 = childWebResource.get(String.class);
        assertEquals(Response.Status.CREATED, response.getResponseStatus());
        assertNotEquals(originalValue1, changedValue1);

    }


    @AfterClass
    public static void oneTimeTearDown() {
        selectorThread.stopEndpoint();
        StartServer.SIRIUS.shutdown();
        File uberStore =  new File(CUSTOM_WRITE_AHEAD_LOG);
        recursiveDelete(uberStore);
        System.out.println("@AfterClass - oneTimeTearDown");
    }

    public static void recursiveDelete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            File[] files =  file.listFiles();
            for (int i = 0; i < files.length; i++) {
                recursiveDelete(files[i]);
            }
        }
        file.delete();
    }


}
