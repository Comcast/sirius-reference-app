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

package com.comcast.xfinity.sirius.refapplication.store;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryDataStore implements DataStore {
    
    public static final MemoryDataStore MDS = new MemoryDataStore();
    private Map<String, Container> containerMap = new HashMap<String, Container>();
    
    public static final JAXBContext CONTEXT = getContext();

    private static JAXBContext getContext() {
        try {
            return JAXBContext.newInstance(Repository.class, Container.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Repository getContainers(){
        Repository c = new Repository();
        List<Container> l = new ArrayList<Container>();
        l.addAll(containerMap.values());
        c.setContainer(l);

        return c;
    }

    public Container getContainer(String container) {
        return containerMap.get(container);
    }

    public boolean hasContainer(String container) {
        return  containerMap.containsKey(container);
    }

    public Container createContainer(Container container) {
        containerMap.put(container.getName(), container);
        return container;
    }

    public Container deleteContainer(String container) {
        Container c = containerMap.remove(container);
        if (c == null) return null;

        return c;
    }

    public boolean hasData(String container, String key) {
        Container c = containerMap.get(container);
        if (c == null) return false;

        return c.getData(key) != null;
    }

    public String getData(String container, String key) {
        Container c = containerMap.get(container);
        if (c == null) return null;

        return c.getData(key);
    }

}
