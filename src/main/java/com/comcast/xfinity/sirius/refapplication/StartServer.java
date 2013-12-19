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
import com.comcast.xfinity.sirius.refapplication.sirius.SiriusImplementation;
import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

public class StartServer {
    protected static Logger logger = LoggerFactory.getLogger(StartServer.class);
    public static final  int DEFAULT_SERVER_PORT = 9998;
    public static final  int DEFAULT_SIRIUS_PORT = 42289;
    public static final URI BASE_URI = UriBuilder.fromUri("http://localhost/storage").port(DEFAULT_SERVER_PORT).build();
    public static int CUSTOM_SERVER_PORT;
    public static int CUSTOM_SIRIUS_PORT;
    public static URI CUSTOM_BASE_URI;
    public static String CUSTOM_WRITE_AHEAD_LOG;
    public static  SiriusImpl SIRIUS;

    public static void main(String[] args) throws IOException {
        if(args.length == 0 ){
            SelectorThread threadSelector = startServer(DEFAULT_SIRIUS_PORT, null, BASE_URI);
            logger.info("Jersey app started with WADL available at %s/application.wadl", BASE_URI);

            System.out.println("Hit return to stop...");
            System.in.read();

            threadSelector.stopEndpoint();
            SIRIUS.shutdown();
            System.exit(0);
        }else{
            for (int i = 0; i < args.length; i++){
                switch (args[i].charAt(0)){
                    case '-':
                        if(args[i].contains("siriusport")){
                            CUSTOM_SIRIUS_PORT = Integer.parseInt(args[i+1]);
                        }
                        if(args[i].contains("serverport")){
                            CUSTOM_SIRIUS_PORT = Integer.parseInt(args[i+1]);
                        }
                    default:
                        throw new IllegalArgumentException("Not a valid arguments: Usage should be " +
                                "[StartServer -siriusport [port] -serverport [port]");

                }
            }
            CUSTOM_BASE_URI = UriBuilder.fromUri("http://localhost/storage").port(CUSTOM_SERVER_PORT).build();
            SelectorThread threadSelector = startServer(CUSTOM_SIRIUS_PORT, CUSTOM_WRITE_AHEAD_LOG, CUSTOM_BASE_URI);
            logger.info("Jersey app started with WADL available at %s/application.wadl", CUSTOM_BASE_URI);

            System.out.println("Hit return to stop...");
            System.in.read();

            threadSelector.stopEndpoint();
            SIRIUS.shutdown();
            System.exit(0);
        }
    }

    /**
     * Start the Reference Application Grizzly HTTP server.
     * @return SelectorThread uses the Grizzly selector thread.
     * @throws java.io.IOException if there is any error starting the server.
     *
     */
    protected static SelectorThread startServer(int siriusPort, String siriusLog, URI BASE_URI) throws IOException {
        final Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "com.comcast.xfinity.sirius.refapplication.endpoints");

        SIRIUS = new SiriusImplementation().startSirius(siriusPort, siriusLog);

        logger.info("Starting Service with the Jersey Java reference container");
        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(BASE_URI, initParams);
        return threadSelector;
    }

}
