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

/**
 * requires sirius project to be installed (i.e. maven install)
 *
 * For more logging uncomment slf4j-simple and comment out slf4j-nop
 *
 * Installing groovy via bash:
 *      curl -s get.gvmtool.net | bash
 *      source ~/.gvm/bin/gvm-init.sh
 *      gvm install groovy
 *
 * Most package managers have groovy (ie brew install groovy)
 *
 * To run: groovy siriExample.groovy
 *
 * The script can also run multiple nodes by specifying a cluster
 * config file. Assuming you are running on ports 2552, 2553 and 2554,
 * create a config file that contains the following three lines:
 *
 *    akka.tcp://sirius-system@localhost:2552/user/sirius
 *    akka.tcp://sirius-system@localhost:2553/user/sirius
 *    akka.tcp://sirius-system@localhost:2554/user/sirius
 *
 * Then run the following in three separate terminals:
 *
 *    terminal1 -> groovy siriusExample.groovy /path/to/config 2552
 *    terminal2 -> groovy siriusExample.groovy /path/to/config 2553
 *    terminal3 -> groovy siriusExample.groovy /path/to/config 2554
 *
 * Pick a terminal for adding data. When running 'put' commands, you
 * should see the data added in the other terminals as well.
 */
@Grab(group = 'com.comcast.xfinity', module = 'sirius', version = '1.2.0-SNAPSHOT')
@Grab(group = 'org.slf4j', module = 'slf4j-nop', version = '1.7.0')
//@Grab(group = 'org.slf4j', module = 'slf4j-simple', version = '1.7.0')
import com.comcast.xfinity.sirius.api.*
import com.comcast.xfinity.sirius.api.impl.*
import java.util.concurrent.*

uberStore = null
backend = null
setupUberStore()
sirius = startSirius()
usage()
handleInput()

def handleInput() {
    def scanner = new Scanner(System.in)

    while (true) {
        print "> "

        def line

        //need to make sure this isn't blocking, otherwise akka won't work
        while (!scanner.hasNextLine()) {
            Thread.sleep(200)
        }
        line = scanner.nextLine()
        def commandLine = line.split(/\s+/)
        boolean commandSupported


        try {
            commandSupported = handleAdministration(commandLine) || handleData(commandLine)
        } catch (Throwable t) {
            t.printStackTrace()
            println "[$line] failed to run"
            commandSupported = true
        }

        if (!commandSupported && line) {
            println "[$line] is not valid"
        }
    }
}

def usage() {
    println """
Please enter a command:

 - data commands
   > get <key>          gets value for a key
   > put <key> <value>  creates or updates value for key
   > delete <key>       deletes value for key
   > data               prints all data

 - administration commands
   > help               prints this message
   > stop               stops sirius
   > start              starts sirius
   > exit               exits the program
"""
}

SiriusConfiguration createConfig() {
    def clusterConfig
    def port = 2552
    if(args && args.length == 2) {
        port = Integer.valueOf(args[1])
        println "using user specified port [$port]"
    }

    if (args) {
        clusterConfig = new File(args[0])
        if(!clusterConfig.exists()) {
            println "config file [$clusterConfig] does not exist"
            System.exit(1)
        }
    }
    else {
        clusterConfig = File.createTempFile("cluster", "config")
        clusterConfig.write("akka.tcp://sirius-system@localhost:$port/user/sirius")
        clusterConfig.deleteOnExit()
    }

    println "cluster config stored at [${clusterConfig.path}]"
    def siriusConfig = new SiriusConfiguration()

    siriusConfig.with {
        setProp(HOST(), "localhost")
        setProp(PORT(), port)
        setProp(CLUSTER_CONFIG(), clusterConfig.path)
        setProp(LOG_LOCATION(), uberStore.path)
    }

    return siriusConfig
}

public class DefaultRequestHandler implements RequestHandler {
    private static final String OK = "ok"

    ConcurrentHashMap backend = [:]

    @Override
    public SiriusResult handleGet(String key) {
        String value = backend.get(key)

        if (value == null) {
            println "[$key] does not have a value"
            return SiriusResult.none()
        }

        println "value for [$key] is [$value]"
        return SiriusResult.some(value)
    }

    @Override
    public SiriusResult handlePut(String key, byte[] body) {
        def value = new String(body)
        backend.put(key, value)

        println "added value [$value] to backend with key [$key]"
        return SiriusResult.some(OK)
    }

    @Override
    public SiriusResult handleDelete(String key) {
        def value = backend.remove(key)

        if (value) {
            println "deleted value [$value] for [$key]"
        } else {
            println "key [$key] does not exist"
        }

        return SiriusResult.some(OK)
    }

}

def awaitBoot(SiriusImpl siriusImpl, Long timeout) {
    long start = new Date().time
    println "Waiting for sirius to boot."
    Long waitTime = System.currentTimeMillis() + timeout
    Long sleepTime = 100L
    while (!siriusImpl.isOnline() && System.currentTimeMillis() < waitTime) {
        Thread.sleep(sleepTime)
    }

    if (!siriusImpl.isOnline()) {
        throw new IllegalStateException("Sirius failed to boot in " + timeout + "ms")
    }

    long end = new Date().time
    println "sirius finished booting in [${end - start}] milliseconds"

}

Sirius startSirius() {
    backend = [:] as ConcurrentHashMap
    SiriusImpl siriusImpl = SiriusFactory.createInstance(
            new DefaultRequestHandler(backend: backend),
            createConfig()
    )
    awaitBoot(siriusImpl, 60000L)
    return siriusImpl
}


def stopSirius(SiriusImpl siriusImpl) {
    siriusImpl.shutdown()
    backend = null
}

boolean handleAdministration(String[] commandLine) {
    switch (commandLine[0]) {
        case "exit":
            if (sirius.isOnline()) {
                stopSirius(sirius)
            }
            System.exit(0)
        case "start":
            if (!sirius.isOnline()) {
                sirius = startSirius()
            } else {
                println "sirius is already online"
            }
            return true
        case "stop":
            if (sirius.isOnline()) {
                stopSirius(sirius)
                println "sirius is offline"
            } else {
                println "sirius is already offline"
            }
            return true
        case "help":
            usage()
            return true
        default:
            return false
    }
}

boolean handleData(String[] commandLine) {

    boolean isDataCommand = false
    if (commandLine.size() > 0) {
        isDataCommand = ["get", "put", "delete", "data"].contains(commandLine[0])
    }

    if (!isDataCommand) {
        return false
    }

    if (!sirius.isOnline()) {
        println "can't perform [${commandLine[0]}], sirius is not online"
        return true
    }

    switch (commandLine[0]) {

        case "get":
            if (commandLine.size() != 2) {
                println "get requires a key"
                break
            }
            def key = commandLine[1]
            def value = backend[key]
            if (value) {
                println "value for [$key] is [$value]"
            } else {
                println "there is no value for [$key]"
            }

            return true
        case "put":
            if (commandLine.size() != 3) {
                println "put requires a key and value"
                return true
            }
            def key = commandLine[1]
            def value = commandLine[2]
            sirius.enqueuePut(key, value.bytes).get(5, TimeUnit.SECONDS)
            return true
        case "delete":
            if (commandLine.size() != 2) {
                println "delete requires a key"
                return true
            }
            def key = commandLine[1]
            sirius.enqueueDelete(key).get(5, TimeUnit.SECONDS)
            return true
        case "data":
            if (!backend) {
                println "There is no data"
                return true
            }

            println "All Data:"
            backend.each {
                println "  ${it.key} -> ${it.value}"
            }
            return true
        default:
            return false
    }
}

def setupUberStore() {
    uberStore = File.createTempFile("uberStore", null)

    //Sometimes this is needed on windows to release file resources so delete() will work
    System.gc()

    def uberCreationFailed = "uber store creation failed"
    assert uberStore.delete(): uberCreationFailed
    assert uberStore.mkdir(): uberCreationFailed
    println "uber stored at [$uberStore]"

    //don't want to clutter up someone's file system
    addShutdownHook {
        if (!uberStore.deleteDir()) {
            println "WARN - Could not delete the temporary uber store at [$uberStore]"
        }
    }
}