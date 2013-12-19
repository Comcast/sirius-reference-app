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
package com.comcast.xfinity.sirius.refapplication.sirius;

import com.comcast.xfinity.sirius.api.RequestHandler;
import com.comcast.xfinity.sirius.api.SiriusResult;
import com.comcast.xfinity.sirius.refapplication.store.Container;
import com.comcast.xfinity.sirius.refapplication.store.MemoryDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RefRequestHandler implements RequestHandler {
    protected Logger logger;
    protected static final String OK = "ok";

    public RefRequestHandler(){
        logger = LoggerFactory.getLogger(this.getClass());
    }
    /**
     * HandleGet
     * @param key
     * @return SiriusResult
     */
    public SiriusResult handleGet(String key){
        logger.trace("Handling a Get for:", key);
        if (MemoryDataStore.MDS.getContainer(key) != null){
            return SiriusResult.some(MemoryDataStore.MDS.getContainer(key));
        }
        else{
            System.out.println("Returning: NONE ");
            return SiriusResult.none();
        }
    }

    /**
     * HandlePut
     * @param key
     * @param body
     * @return SiriusResult
     */
    public SiriusResult handlePut(String key, byte[] body){
        logger.trace("Handling a PUT {}-size:{}", key, body);

        Container container = Container.deserialize(body);
        MemoryDataStore.MDS.createContainer(container);
        if(container != null){
           return SiriusResult.some(OK);
        }
        else{
            return SiriusResult.none();
        }

    }

    /**
     * handleDelete
     * @param key
     * @return SiriusResult
     */
    public SiriusResult handleDelete(String key){
        logger.trace("Handling a DELETE ", key);
        Container container = MemoryDataStore.MDS.deleteContainer(key);
        if (container != null){
            return SiriusResult.some(container);
        }
        else{
            return SiriusResult.none();
        }
    }

}
