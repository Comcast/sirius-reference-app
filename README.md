Sirius-Reference-App
A simple reference application that helps developers get started with using the Sirius library


Getting Started Tutorial

This tutorial used code snippets from a simple application that uses the Sirius library to provide
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

Run the example as follows

Default Server port is 	9998
```
    mvn clean compile exec:java
```
Or
```
    mvn clean package
    cd target
    java -jar sirius-reference-app-0.1.1-jar-with-dependencies.jar
```

Access the application WADL
```
    curl http://localhost:9998/storage/application.wadl
```
Get the high level keys in the data store
```
    curl http://localhost:9998/storage/repository
```
Add a Key
```
    curl -X PUT http://localhost:9998/storage/repository/Stations
```
Add some content
```
    curl -X PUT -HContent-type:text/plain --data "Six Feet Under"  http://127.0.0.1:9998/storage/repository/Stations/HBO
```

The mapping of the URI path space is presented in the following table:
| URL                           | Resource Class                        | HTTP Methods
|-------------------------------|:-------------------------------------:|-----------------:|
|/storage                       | Resource Class = RepositoryController | GET              |
|                               |                                       |                  |
|/storage/repository/           |/storage/repository/                   | GET, PUT, DELETE |
|                               |                                       |                  |
|/storage/repository/{container}| ContainerController                   | GET, PUT, DELETE |
|