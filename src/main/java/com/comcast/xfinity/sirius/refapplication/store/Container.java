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
package com.comcast.xfinity.sirius.refapplication.store;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.*;

@XmlRootElement
public class Container implements Serializable{

    private String name;
    private String uri;
    private Map<String,String> data;

    public Container() {
    }

    public Container(String name, String uri) {
        setName(name);
        setUri(uri);
        setData(new HashMap<String, String>());

    }

    public Container(String name, String uri, Map<String,String> data) {
        setName(name);
        setUri(uri);
        setData(data);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Map<String,String> getData() {
        return data;
    }

    public String getData(String key) {
        return this.data.get(key);
    }

    public void setData(Map<String,String> data) {
        this.data = data;
    }

    public void putData(String key ,String body) {

        if(!this.data.containsKey(key)){
            this.data.put(key, body);
            return;
        }
        this.data.remove(key);
        this.data.put(key,body);
    }

    public String removeData(String key){
        this.data.remove(key);
        return key;
    }

    public Container clone() {
        Container that = new Container(this.name, this.uri, this.data);
        return that;
    }

    public byte[] serialize() {
        byte[] bytes = null;
        try{
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(this);
            bytes = b.toByteArray();
        } catch (IOException iOE){

        }
        return bytes;
    }

    public static Container deserialize(byte[] bytes) {
        Object object = null;
        try{
            ByteArrayInputStream b = new ByteArrayInputStream(bytes);
            ObjectInputStream o = new ObjectInputStream(b);
            object = o.readObject();
        }catch(IOException iOE){

        }catch(ClassNotFoundException cNFE){

        }
        return (Container) object;
    }
}
