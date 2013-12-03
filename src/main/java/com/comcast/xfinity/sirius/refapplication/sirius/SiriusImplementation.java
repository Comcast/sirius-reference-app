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

package com.comcast.xfinity.sirius.refapplication.sirius;

import com.comcast.xfinity.sirius.api.RequestHandler;
import com.comcast.xfinity.sirius.api.SiriusConfiguration;
import com.comcast.xfinity.sirius.api.impl.SiriusFactory;
import com.comcast.xfinity.sirius.api.impl.SiriusImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created By Core Application Platforms Comcast
 * User: mbyron200
 * Date: 11/7/13
 * <p/>
 * .
 */

public class SiriusImplementation {

    private String defaultWriteAheadLog;
    private String clusterConfig;
    private RefRequestHandler requestHandler;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());


    public SiriusImpl startSirius(int port, String customWriteAheadLog){
        if(customWriteAheadLog != null){
            if(!customWriteAheadLog.isEmpty()){
                defaultWriteAheadLog = createSiriusLog(customWriteAheadLog);
                clusterConfig = createClusterConfig("config/",port);
                requestHandler = createRequestHandler();
                return new SiriusImplementation().initializeSirius(requestHandler, defaultWriteAheadLog,
                                                clusterConfig, port);
            }
        }
        defaultWriteAheadLog = createSiriusLog("uberStore/");
        clusterConfig = createClusterConfig("config/",port);
        requestHandler = createRequestHandler();

        return new SiriusImplementation().initializeSirius(requestHandler, defaultWriteAheadLog, clusterConfig,port);
    }
    /**
    * Creates a SiriusImpl and waits until it has completed its bootstrap procedure and is online
    *
    * @param requestHandler An implemented RequestHandler that's required
    * @param siriusLog Location on filesystem where the Sirius WAL will be found
    * @param clusterConfig Location on the filesystem where Sirius cluster config will be found
    * @param siriusPort port for Sirius to use to communicate with other cluster members
    * @return a Sirius instance that is ready to start accepting requests
    */
    public SiriusImpl initializeSirius(RequestHandler requestHandler, String siriusLog, String clusterConfig,
                                  int siriusPort){

        String localHost = "localhost";

        //Basic Cluster Configuration
        SiriusConfiguration siriusConfig = new SiriusConfiguration();
        siriusConfig.setProp(SiriusConfiguration.HOST(), localHost);
        siriusConfig.setProp(SiriusConfiguration.PORT(), siriusPort);
        siriusConfig.setProp(SiriusConfiguration.AKKA_SYSTEM_NAME(), "sirius-"+siriusPort);
        siriusConfig.setProp(SiriusConfiguration.CLUSTER_CONFIG(), clusterConfig);
        siriusConfig.setProp(SiriusConfiguration.PAXOS_MEMBERSHIP_CHECK_INTERVAL(), 0.1);
        siriusConfig.setProp(SiriusConfiguration.REPROPOSAL_WINDOW(), 10);
        siriusConfig.setProp(SiriusConfiguration.LOG_REQUEST_CHUNK_SIZE(), 100);
        siriusConfig.setProp(SiriusConfiguration.LOG_LOCATION(), siriusLog);


        logger.info( "Firing up Sirius on " + localHost + ":" + siriusPort);

        SiriusImpl siriusImpl = SiriusFactory.createInstance(requestHandler, siriusConfig);
        isBooted(siriusImpl, 60000L);
        return siriusImpl;
    }

    private static RefRequestHandler createRequestHandler(){
        return new RefRequestHandler();
    }

    private static String createClusterConfig(String location, int port) {
        File membershipFile = new File(location, "cluster.conf");
        File dir =  new File(location);
        dir.mkdirs();
        try {
            membershipFile.createNewFile();
            FileOutputStream fOS = new FileOutputStream(membershipFile);
            fOS.write(("akka://sirius-"+port+"@localhost:"+port+"/user/sirius\n").getBytes());
            fOS.close();
        }catch(IOException ioe){

        }
        return membershipFile.getAbsolutePath();
    }

    private static String createSiriusLog(String location){
        File uberStoreDir = new File(location);
        if (!uberStoreDir.exists()) {
          uberStoreDir.mkdirs();
        }

        //File uberstoreFile = new File(uberStoreDir, UUID.randomUUID().toString());
        File uberstoreFile = new File(uberStoreDir,"sirius-42289");
            uberstoreFile.mkdir();
        return uberstoreFile.getAbsolutePath();
    }

    private void isBooted(SiriusImpl siriusImpl , Long timeout){
        System.out.println("Waiting for sirius to boot.");
        Long waitTime = System.currentTimeMillis() + timeout;
        final Long sleepTime = 5L;
        while (!siriusImpl.isOnline() && System.currentTimeMillis() < waitTime) {
          try {
            logger.info("Waiting for sirius to boot.");
            Thread.sleep(sleepTime);
          }catch(InterruptedException ie) {
            logger.info("Failed while waiting for sirius to boot.");
          }
        }
        if (!siriusImpl.isOnline() ) {
            throw new IllegalStateException("Sirius failed to boot in " + timeout + "ms");
        }
      }

    public void shutdown(SiriusImpl siriusImpl){
        siriusImpl.shutdown();
    }
}
