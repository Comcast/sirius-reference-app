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

import com.comcast.xfinity.sirius.api.impl.status.NodeStats;
import com.comcast.xfinity.sirius.refapplication.RefAppState;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

@Path("/")
public class StatusController {

    /**
     * Dump of Sirius status. Both node configuration and runtime statistics appear
     * here.
     *
     * @return text/plain response, dump of full node status
     * @throws Exception
     */
    @GET
    @Path("sirius-status")
    public Response getSiriusStatus() throws Exception {
        NodeStats.FullNodeStatus siriusStatus = RefAppState.sirius.getStatus().get(5, TimeUnit.SECONDS);

        return Response.ok(siriusStatus.toString(), MediaType.TEXT_PLAIN_TYPE).build();
    }

    @GET
    @Path("status")
    public Response getStatus() throws Exception {
        long successful = RefAppState.successfulPuts.get();
        long total = RefAppState.totalPuts.get();
        String output = "successfulPuts: " + successful + "\n";
        output += "failedPuts: " + (total - successful) + "\n";

        return Response.ok(output, MediaType.TEXT_PLAIN_TYPE).build();
    }

    /**
     * Dump of all the keys in the repository. Be a little careful with this, if you're planning
     * to jam a bunch of stuff in here. There could be a lot of keys. This isn't exactly advisable
     * in a real application.
     *
     * @return text/plain response, all the keys in the repository
     */
    @GET
    @Path("keys")
    public Response getKeys() {
        StringBuilder response = new StringBuilder();
        for (String key : RefAppState.repository.keys()) {
            response.append(key).append("\n");
        }
        return Response.ok(response.toString()).build();
    }

    /**
     * No-op response, to test whether the webapp is up and running.
     *
     * @return 200, text/plain "ok"
     */
    @GET
    @Path("test")
    public Response test() {
        return Response.ok("ok").build();
    }
}
