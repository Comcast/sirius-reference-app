/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.comcast.xfinity.sirius.refapplication.endpoints;

import com.comcast.xfinity.sirius.api.Sirius;
import com.comcast.xfinity.sirius.api.SiriusResult;
import com.comcast.xfinity.sirius.refapplication.StartServer;
import com.comcast.xfinity.sirius.refapplication.store.Container;
import com.comcast.xfinity.sirius.refapplication.store.MemoryDataStore;
import com.sun.jersey.api.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Produces("application/xml")
public class ContainerController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    String container;

    private final Sirius sirius = StartServer.SIRIUS;

    public ContainerController(UriInfo uriInfo, Request request, String container ) {
       this.uriInfo   = uriInfo;
       this.request   = request;
       this.container = container;
   }

    @GET
    public Container getContainer(@QueryParam("search") String search)throws InterruptedException, ExecutionException {
        logger.info("GET CONTAINER " + container + ", search = " + search);
        Future<SiriusResult> future = sirius.enqueueGet(container);
        SiriusResult result = future.get();

        if (!result.hasValue()){
            System.out.println("Container not found");
            throw new NotFoundException("Container not found");
        }

        Container c = (Container)result.getValue();

        if (search != null) {
            c = c.clone();
            Map<String,String> i = c.getData();

            if (i.containsKey(search)){
                c = new Container();
                Map<String,String> map = new HashMap<String, String>();
                map.put(search, i.get(search));
                c.setData(map);
            }
            return c;
        }
        return c;
    }

    @PUT
    public Response putContainer()throws InterruptedException, ExecutionException {
        logger.info("PUT CONTAINER " + container);
        URI uri =  uriInfo.getAbsolutePath();
        int lastIndex = uri.toString().lastIndexOf("/");
        if(lastIndex != -1){
            String newURI = uri.toString().substring(0,lastIndex);
            uri = URI.create(newURI);
        }
        Container c = new Container(container, uri.toString());

        Response r;
        if (!MemoryDataStore.MDS.hasContainer(c.getName())) {
            r = Response.created(uri).build();
            byte[] bytes = c.serialize();
            Future < SiriusResult > future = sirius.enqueuePut(c.getName(), bytes);
            future.get();
        } else {
            r = Response.noContent().build();

        }
        return r;
    }

    @PUT
    @Path("{key}")
    public Response putData(@PathParam("key") String key, String data)throws InterruptedException, ExecutionException {
        logger.info("PUT CONTAINER " + container +" With KEY: "+key+" & DATA: "+data);
        URI uri =  uriInfo.getAbsolutePath();
        int lastIndex = uri.toString().lastIndexOf("/");
        if(lastIndex != -1){
            String newURI = uri.toString().substring(0,lastIndex);
            uri = URI.create(newURI);
        }
        Container c = new Container(container, uri.toString());
        Response r;

        Map<String,String> map;
        try{
            if (!MemoryDataStore.MDS.hasData(container, key)) {
                r = Response.created(uri).build();
                map = MemoryDataStore.MDS.getContainer(container).getData();
                map.put(key,data);
                c.setData(map);
                byte[] bytes = c.serialize();
                Future < SiriusResult > future = sirius.enqueuePut(c.getName(), bytes);
                future.get();
            } else {
                r = Response.noContent().build();
                c = MemoryDataStore.MDS.getContainer(container);
                map = MemoryDataStore.MDS.getContainer(container).getData();
                map.put(key, data);
                c.setData(map);
                byte[] bytes = c.serialize();
                Future < SiriusResult > future = sirius.enqueuePut(c.getName(), bytes);
                future.get();
            }
        }catch(NullPointerException nPE){
            logger.info("The enclosing Container and key have not been created",nPE);
        }
        r = Response.created(uri).build();
        return r;
    }

    @DELETE
    public void deleteContainer()throws InterruptedException, ExecutionException {
        logger.info("DELETE CONTAINER " + container);
        Future <SiriusResult> future = sirius.enqueueDelete(container);
        SiriusResult result = future.get();
        if (result == null)
            throw new NotFoundException("Container not found");
    }

}
