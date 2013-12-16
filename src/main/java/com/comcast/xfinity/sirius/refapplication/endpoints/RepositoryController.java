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
package com.comcast.xfinity.sirius.refapplication.endpoints;

import com.comcast.xfinity.sirius.api.SiriusResult;
import com.comcast.xfinity.sirius.api.impl.status.NodeStats;
import com.comcast.xfinity.sirius.refapplication.RefAppState;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@Path("/")
public class RepositoryController {

    /**
     * Dump of Sirius status. Both node configuration and runtime statistics appear
     * here.
     *
     * @return text/plain response, dump of full node status
     * @throws Exception
     */
    @GET
    @Path("sirius-status")
    public Response getStatus() throws Exception {
        NodeStats.FullNodeStatus siriusStatus = RefAppState.sirius.getStatus().get(5, TimeUnit.SECONDS);

        return Response.ok(siriusStatus.toString(), MediaType.TEXT_PLAIN_TYPE).build();
    }

    @GET
    @Path("keys")
    public Response getKeys() throws Exception {
        StringBuilder response = new StringBuilder();
        for (String key : RefAppState.repository.keys()) {
            response.append(key).append("\n");
        }
        return Response.ok(response.toString()).build();
    }

    @GET
    @Path("{key: .+}")
    public Response getEntry(@PathParam("key") String key) {
        String value = RefAppState.repository.get(key);

        if (value == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(value, MediaType.TEXT_PLAIN_TYPE).build();
    }

    /**
     * PUT an entry. Returns immediately, does not wait for the result. If you
     * want this to block, expose any errors found, and return a more accurate result,
     * see deleteEntry() below.
     *
     * @param key key of PUT, from the url path
     * @param data body of PUT
     * @return 202 Accepted in all cases.
     */
    @PUT
    @Path("{key: .+}")
    public Response putEntry(@PathParam("key") String key, String data) {
        RefAppState.sirius.enqueuePut(key, data.getBytes());

        return Response.status(Response.Status.ACCEPTED).build();
    }

    /**
     * DELETE an entry. This example will await the result of the delete, returning
     * a 200 OK if everything works out, a 500 otherwise.
     *
     * @param key key to delete
     * @return 200 OK, unless badness happens
     * @throws Exception generic exception wrapping any of the bad things that could happen:
     *                   TimeoutException, ExecutionException, InterruptedException
     */
    @DELETE
    @Path("{key: .+}")
    public Response deleteEntry(@PathParam("key") String key) throws Exception {
        // just throw any exceptions that come out. it'll result in a 500, which is accurate.
        awaitResult(RefAppState.sirius.enqueueDelete(key));

        // if we make it here, the delete succeeded
        return Response.status(Response.Status.OK).build();
    }

    public void awaitResult(Future<SiriusResult> future) throws Exception {
        SiriusResult result = future.get(5, TimeUnit.SECONDS);

        // if there is a result inside, dig it out. if it's an exception, it will be thrown here.
        if (result.hasValue()) {
            result.getValue();
        }
    }
}
