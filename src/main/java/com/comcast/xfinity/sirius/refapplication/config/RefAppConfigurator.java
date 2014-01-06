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

package com.comcast.xfinity.sirius.refapplication.config;

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

    public static final String SIRIUS_PORT_KEY = "sirius.port";
    public static final String SIRIUS_HOSTNAME_KEY = "sirius.hostname";
    public static final String CLUSTER_CONFIG_KEY = "cluster-config.location";
    public static final String UBERSTORE_KEY = "uberstore.location";
    public static final String SERVER_PORT_KEY = "server.port";

    private final String akkaSystemName;
    private final int serverPort;
    private final int siriusPort;
    private final String siriusHostName;
    private final String clusterConfigLocation;
    private final String uberStoreLocation;

    public RefAppConfigurator(String configLocation) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(configLocation)));

        serverPort = Integer.parseInt(properties.getProperty(SERVER_PORT_KEY));
        siriusPort = Integer.parseInt(properties.getProperty(SIRIUS_PORT_KEY));
        siriusHostName = properties.getProperty(SIRIUS_HOSTNAME_KEY, Inet4Address.getLocalHost().getHostAddress());
        akkaSystemName = "sirius-" + siriusPort;
        clusterConfigLocation = properties.getProperty(CLUSTER_CONFIG_KEY);
        uberStoreLocation = properties.getProperty(UBERSTORE_KEY);
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getSiriusPort() {
        return siriusPort;
    }

    public String getSiriusHostName() {
        return siriusHostName;
    }

    public String getClusterConfigLocation() {
        return clusterConfigLocation;
    }

    public String getUberStoreLocation() {
        return uberStoreLocation;
    }

    /**
     * Get the path of this server, for use in cluster.config
     *
     * @return akka path of this sirius node
     */
    public String getAkkaPath() {
        StringBuilder builder = new StringBuilder("");
        builder.append("akka.tcp://");
        builder.append(akkaSystemName).append("@");
        builder.append(siriusHostName).append(":");
        builder.append(siriusPort);
        builder.append("/user/sirius");

        return builder.toString();
    }

    /**
     * Using the properties passed in, construct a SiriusConfig to send to the SiriusFactory.
     *
     * @return SiriusConfiguration with required properties
     */
    public SiriusConfiguration buildSiriusConfig() {
        SiriusConfiguration siriusConfig = new SiriusConfiguration();
        siriusConfig.setProp(SiriusConfiguration.HOST(), siriusHostName);
        siriusConfig.setProp(SiriusConfiguration.PORT(), siriusPort);
        siriusConfig.setProp(SiriusConfiguration.AKKA_SYSTEM_NAME(), akkaSystemName);
        siriusConfig.setProp(SiriusConfiguration.CLUSTER_CONFIG(), clusterConfigLocation);
        siriusConfig.setProp(SiriusConfiguration.LOG_LOCATION(), uberStoreLocation);

        return siriusConfig;
    }

}
