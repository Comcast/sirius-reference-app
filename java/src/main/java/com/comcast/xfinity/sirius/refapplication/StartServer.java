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

import com.comcast.xfinity.sirius.api.impl.SiriusFactory;
import com.comcast.xfinity.sirius.api.impl.SiriusImpl;
import com.comcast.xfinity.sirius.refapplication.config.RefAppConfigurator;
import com.comcast.xfinity.sirius.refapplication.sirius.DefaultRequestHandler;
import com.comcast.xfinity.sirius.refapplication.store.KVRepository;
import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class StartServer {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            usage();
        }

        RefAppConfigurator configurator = new RefAppConfigurator(args[0]);

        RefAppState.repository = new KVRepository();
        RefAppState.sirius = startSirius(configurator);
        RefAppState.successfulPuts = new AtomicLong();
        RefAppState.totalPuts = new AtomicLong();

        runServer(configurator);

        stopSirius(RefAppState.sirius);
        System.exit(0);
    }

    public static void usage() {
        throw new IllegalArgumentException("Usage:\n StartServer [config-location]");
    }

    private static void runServer(RefAppConfigurator configurator) throws IOException, InterruptedException {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(configurator.getServerPort()).build();

        Map<String, String> params = new HashMap<String, String>();
        params.put("com.sun.jersey.config.property.packages", "com.comcast.xfinity.sirius.refapplication.endpoints");

        SelectorThread thread = GrizzlyWebContainerFactory.create(baseUri, params);

        System.out.println();
        System.out.println("Server fired up, using akka over TCP. Akka address for this server:");
        System.out.println(configurator.getAkkaPath());
        System.out.println("Hit ctrl-c to quit.");
        try {
            while (true) {
                // put the local thread to sleep, let grizzly do its thing
                Thread.sleep(1000L);
            }
        } finally {
            thread.stopEndpoint();
        }
    }

    /**
     * Start an instance of Sirius. Will wait a maximum of 60 seconds to boot. Make this configurable
     * if you're going to have a lot of data in here.
     *
     * @return SiriusImpl ready to use
     */
    public static SiriusImpl startSirius(RefAppConfigurator refAppConfigurator) {
        SiriusImpl siriusImpl = SiriusFactory.createInstance(new DefaultRequestHandler(), refAppConfigurator.buildSiriusConfig());
        awaitBoot(siriusImpl, 60000L);

        return siriusImpl;
    }

    /**
     * Wait for Sirius to bootstrap. This includes replaying the entire WAL.
     *
     * @param siriusImpl sirius instance to test
     * @param timeout how long you're willing to wait for Sirius to boot
     */
    private static void awaitBoot(SiriusImpl siriusImpl, Long timeout) {
        System.out.println("Waiting for sirius to boot.");
        Long waitTime = System.currentTimeMillis() + timeout;
        Long sleepTime = 100L;
        while (!siriusImpl.isOnline() && System.currentTimeMillis() < waitTime) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // we're the only ones here, nobody's going to interrupt us.
                // and if it does happen, well, just keep waiting for sirius to start anyway.
            }
        }

        if (!siriusImpl.isOnline() ) {
            throw new IllegalStateException("Sirius failed to boot in " + timeout + "ms");
        }
    }

    public static void stopSirius(SiriusImpl siriusImpl){
        siriusImpl.shutdown();
    }
}

