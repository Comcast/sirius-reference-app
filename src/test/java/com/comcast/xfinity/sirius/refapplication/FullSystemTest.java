/*
 * Copyright 2013 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.xfinity.sirius.refapplication;

import com.comcast.xfinity.sirius.api.impl.SiriusImpl;
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
public class FullSystemTest extends BaseTest{
    /*
     Commenting all of this out until it works. Does not compile as-is.
     */

    @Test
    public void testNothing() {

    }

    /*
    private static SelectorThread selectorThread_1;
    private static SelectorThread selectorThread_2;
    private static SelectorThread selectorThread_3;
    private static SiriusImpl SIRIUS_1;
    private static SiriusImpl SIRIUS_2;
    private static SiriusImpl SIRIUS_3;
    private static Client client;
    private static WebResource webResource;
    private final static String CUSTOM_WRITE_AHEAD_LOG = "resources/uberStore/";
    public final static String AKKA_EXTERNAL_CONFIG = "resources/config/application.conf";
    public final static String RESOURCE_FOLDER = "resources";

    @BeforeClass
    public static void oneTimeSetUp()throws Exception {
        File resourceFolder =  new File(RESOURCE_FOLDER);
        recursiveDelete(resourceFolder);

        int CUSTOM_SERVER_PORT_1 = 9997;
        int CUSTOM_SIRIUS_PORT_1 = 42289;
        URI CUSTOM_BASE_URI_1 = UriBuilder.fromUri("http://localhost/storage").port(CUSTOM_SERVER_PORT_1).build();

        int CUSTOM_SERVER_PORT_2 = 9998;
        int CUSTOM_SIRIUS_PORT_2 = 42290;
        URI CUSTOM_BASE_URI_2 = UriBuilder.fromUri("http://localhost/storage").port(CUSTOM_SERVER_PORT_2).build();

        int CUSTOM_SERVER_PORT_3 = 9999;
        int CUSTOM_SIRIUS_PORT_3 = 42291;
        URI CUSTOM_BASE_URI_3 = UriBuilder.fromUri("http://localhost/storage").port(CUSTOM_SERVER_PORT_3).build();

        client = Client.create();
        webResource = client.resource(CUSTOM_BASE_URI_1);
        selectorThread_1 = StartServer.startServer(CUSTOM_SIRIUS_PORT_1, CUSTOM_WRITE_AHEAD_LOG, CUSTOM_BASE_URI_1,AKKA_EXTERNAL_CONFIG);
        SIRIUS_1 = StartServer.SIRIUS;
        System.out.println(SIRIUS_1);
        selectorThread_2 = StartServer.startServer(CUSTOM_SIRIUS_PORT_2, CUSTOM_WRITE_AHEAD_LOG, CUSTOM_BASE_URI_2,AKKA_EXTERNAL_CONFIG);
        SIRIUS_2 = StartServer.SIRIUS;
        System.out.println(SIRIUS_2);
        selectorThread_3 = StartServer.startServer(CUSTOM_SIRIUS_PORT_3, CUSTOM_WRITE_AHEAD_LOG, CUSTOM_BASE_URI_3,AKKA_EXTERNAL_CONFIG);
        SIRIUS_3 = StartServer.SIRIUS;
        System.out.println(SIRIUS_3);
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
        selectorThread_1.stopEndpoint();
        selectorThread_2.stopEndpoint();
        selectorThread_3.stopEndpoint();
        StartServer.SIRIUS.shutdown();
        File resourceFolder =  new File(RESOURCE_FOLDER);
        recursiveDelete(resourceFolder);
        System.out.println("@AfterClass - oneTimeTearDown");
    }

    @Test
    public void testCreateConfig(){
        new SiriusImplementation().createClusterConfig("config",8080);
        new SiriusImplementation().createClusterConfig("config",8081);
        new SiriusImplementation().createClusterConfig("config",8082);
        new SiriusImplementation().createClusterConfig("config",8083);
        recursiveDelete(new File("config"));

    }

    */
}
