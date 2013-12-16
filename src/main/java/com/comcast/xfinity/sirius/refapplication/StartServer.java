
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

import com.comcast.xfinity.sirius.refapplication.config.RefAppConfigurator;
import com.comcast.xfinity.sirius.refapplication.store.KVRepository;
import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class StartServer {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            usage();
        }

        RefAppConfigurator configurator = new RefAppConfigurator(args[0]);

        RefAppState.repository = new KVRepository();
        RefAppState.sirius = configurator.startSirius();

        runServer(configurator);

        configurator.stopSirius(RefAppState.sirius);
        System.exit(0);
    }

    public static void usage() {
        throw new IllegalArgumentException("Usage:\n StartServer [config-location]");
    }

    private static void runServer(RefAppConfigurator configurator) throws IOException {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(configurator.getServerPort()).build();
        SelectorThread selectorThread = startServer(baseUri);

        System.out.println();
        System.out.println("Server fired up, using akka over TCP. Akka address for this server:");
        System.out.println(configurator.getAkkaPath());
        System.out.println("Hit enter to stop server...");
        System.in.read();

        stopServer(selectorThread);
    }

    protected static SelectorThread startServer(URI baseUri) throws IOException {
        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "com.comcast.xfinity.sirius.refapplication.endpoints");

        return GrizzlyWebContainerFactory.create(baseUri, initParams);
    }

    private static void stopServer(SelectorThread thread) {
        thread.stopEndpoint();
    }
}
