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
