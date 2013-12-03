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

import com.comcast.xfinity.sirius.refapplication.store.MemoryDataStore;
import com.comcast.xfinity.sirius.refapplication.store.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;


@Path("/repository")
@Produces("application/xml")
public class RepositoryController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @Path("{container}")
    public ContainerController getContainerResource(@PathParam("container") String container) {
        return new ContainerController(uriInfo, request, container);
    }

    @GET
    public Repository getContainers() {
        logger.info("GET CONTAINERS:");
        return MemoryDataStore.MDS.getContainers();
    }
}
