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
package com.comcast.xfinity.sirius.refapplication.config;

import com.comcast.xfinity.sirius.api.Sirius;
import com.comcast.xfinity.sirius.api.SiriusConfiguration;
import com.comcast.xfinity.sirius.api.impl.SiriusFactory;
import com.comcast.xfinity.sirius.api.impl.SiriusImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.Properties;

import com.comcast.xfinity.sirius.refapplication.sirius.DefaultRequestHandler;

public class RefAppConfigurator {

    public static final String SERVER_PORT_KEY = "server.port";

    private final int serverPort;
    private final String akkaPath;
    private final String uberStoreLocation;

    private Properties properties = new Properties();

    public RefAppConfigurator(String configLocation) throws IOException {
        properties.load(new FileInputStream(new File(configLocation)));

        serverPort = Integer.parseInt(properties.getProperty(SERVER_PORT_KEY));
        uberStoreLocation = properties.getProperty(SiriusConfiguration.LOG_LOCATION());


        Integer siriusPort = Integer.parseInt(properties.getProperty(SiriusConfiguration.PORT()));
        String siriusHostName = properties.getProperty(SiriusConfiguration.HOST(), Inet4Address.getLocalHost().getHostAddress());

        akkaPath = new StringBuilder("")
            .append("akka.tcp://")
            .append("sirius-system").append("@")
            .append(siriusHostName).append(":")
            .append(siriusPort)
            .append("/user/sirius").toString();
    }

    public int getServerPort() {
        return serverPort;
    }

    /**
     * Get the path of this server, for use in cluster.config
     *
     * @return akka path of this sirius node
     */
    public String getAkkaPath() {
        return akkaPath;
    }

    /**
     * Using the properties passed in, construct a SiriusConfig to send to the SiriusFactory.
     *
     * @return SiriusConfiguration with required properties
     */
    public SiriusConfiguration buildSiriusConfig() {
        File uberstore = new File(uberStoreLocation);
        if (!uberstore.exists()) {
            uberstore.mkdirs();
        }

        SiriusConfiguration siriusConfig = new SiriusConfiguration();
        for (String name: properties.stringPropertyNames()) {
            siriusConfig.setProp(name, properties.getProperty(name));
        }

        return siriusConfig;
    }

}
