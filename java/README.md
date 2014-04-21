Sirius-Reference-App
======

A simple reference application that helps developers get started with using the Sirius library


Basic interaction with the sample app
======

This section describes firing up and playing with the sample app in single-node, non-distributed
mode. We'll describe more interesting setups later.

Build and run the application. Default server port is 8000. See contents of config.properties (or the PSF
definitions in RefAppConfigurator.java) for available parameters.
```
    mvn clean package
    ./start.sh example/single-node-local/config.properties
```

Add some content:
```
    curl -vX PUT -HContent-type:text/plain --data "Six Feet Under"  http://127.0.0.1:8000/storage/series/12345
    curl -vX PUT -HContent-type:text/plain --data "The Wire"  http://127.0.0.1:8000/storage/series/45678
```

See the keys you've added:
```
    curl http://127.0.0.1:8000/keys
```

Get content back out:
```
    curl http://127.0.0.1:8000/storage/series/12345
```

Delete a k:v pair:
```
    curl -vX DELETE http://127.0.0.1:8000/storage/series/45678
```

See the remaining keys:
```
    curl http://127.0.0.1:8000/keys
```

Available URI paths:

| URL                           | Resource Class                        | HTTP Methods
|-------------------------------|:-------------------------------------:|-----------------:|
|/keys                          | StatusController                      | GET              |
|                               |                                       |                  |
|/test                          | StatusController                      | GET              |
|                               |                                       |                  |
|/sirius-status                 | StatusController                      | GET              |
|                               |                                       |                  |
|/storage/{.+ }                 | RepositoryController                  | GET, PUT, DELETE |

Sample app as a local cluster
======
Included with the reference application is a sample config for a three node cluster, running on a single machine.
This is a toy example, but should hopefully give you an idea of configuring nodes to talk to one another.

You'll need three terminal windows (or screen instances), one for each node, and a fourth to issue commands.
```
    mvn clean package
    # window one
    ./start.sh example/three-node-local/node-one/config.properties
    # window two
    ./start.sh example/three-node-local/node-two/config.properties
    # window three
    ./start.sh example/three-node-local/node-three/config.properties
```

The nodes should spin up and figure out how to talk to one another. It may take up to 30 seconds for them to find
one another, depending on how closely you start them. Note that they share a single `cluster.config` file, located at
```
    example/three-node-local/cluster.config
```
but each have their own uberstore (after you start them up, at least), located at
```
    example/three-node-local/node-one/uberstore
    example/three-node-local/node-two/uberstore
    example/three-node-local/node-three/uberstore
```
Once they're up and running, you can add data to any, and see the results on any other.
```
    # add to first node
    curl -vX PUT -HContent-type:text/plain --data "Six Feet Under"  http://127.0.0.1:8000/storage/series/12345
    curl -vX PUT -HContent-type:text/plain --data "The Wire"  http://127.0.0.1:8000/storage/series/45678
    # see keys from second
    curl http://127.0.0.1:8001/keys
    # delete entry from second
    curl -vX DELETE http://127.0.0.1:8001/storage/series/45678
    # see keys from third
    curl http://127.0.0.1:8002/keys
```


Getting Started Tutorial
======

This tutorial uses code snippets from a simple application that uses the Sirius library to provide
a simple distribute and consistent data store. We will first walk through the steps to write a Sirius powered application.

When developing with Sirius there are four blocks that you need. A Sirius representation, some class
that configures what will be your Sirius Implementation. A RequestHandler class that extends the Sirius
RequestHandle trait/interface, overriding the handleGet, handlePut and handleDelete method. A controller
class that determines when and what data gets written to or read from Sirius. And finally a implementation
of some in memory data store.

Configuring Sirius: Here we configure what the cluster will look like, how many, how often they message
each other etc.
    ```java
    public SiriusImpl initializeSirius(RequestHandler requestHandler,
    		String siriusLog, String clusterConfig, int siriusPort){
    ```

Implementing a RequestHandler: Here we are only interested in the handlePut and handleGet. When you
issue an enqueuePut the underlying Sirius library also fires the corresponding RequestHandler’s handlePut
method. when you issue an enqueueGet the Sirius library then fires the corresponding RequestHandler’s
handleGet method.
   ```java
    public SiriusResult handleGet(String key)
    	map.get(key)
    public SiriusResult handlePut(String key, byte[] data)
    	map.put(key,data)
    public SiriusResult handleDelete(String key)
    	map.remove(key)



    public SiriusResult handlePut(String key, byte[] body){
        logger.trace("Handling a PUT {}-size:{}", key, body);

        Container container = Container.deserialize(body);
        InMemoryDataStore.IMS.createContainer(container);
        if(container != null){
           return SiriusResult.some(OK);
        }
        else{
            return SiriusResult.none();
        }

    }

    public SiriusResult handleDelete(String key){......}
   ```

Storing Data in Sirius: Storing data in Sirius is simple. You can do an enqueueGet, enqueuePut or enqueueDelete.
    ```java
    Future <SiriusResult> future = sirius.enqueuePut(key, bytes)
    Future <SiriusResult> future = sirius.enqueueGet(key)
    ```

