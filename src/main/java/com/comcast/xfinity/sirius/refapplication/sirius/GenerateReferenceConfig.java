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

import java.io.*;

public class GenerateReferenceConfig {

    static String applicationConf;
    public static void createReferenceConfig(File dest) throws IOException{
        applicationConf =
                        "####################################\n" +
                        "# Akka Actor Reference Config File #\n" +
                        "####################################\n" +
                        "\n" +
                        "# This is the reference config file that contains all the default settings.\n" +
                        "# Make your edits/overrides in your application.conf.\n" +
                        "\n" +
                        "akka {\n" +
                        "  # Akka version, checked against the runtime version of Akka.\n" +
                        "  version = \"2.0.2\"\n" +
                        "\n" +
                        "  # Home directory of Akka, modules in the deploy directory will be loaded\n" +
                        "  home = \"\"\n" +
                        "\n" +
                        "  # Event handlers to register at boot time (Logging$DefaultLogger logs to STDOUT)\n" +
                        "  event-handlers = [\"akka.event.Logging$DefaultLogger\"]\n" +
                        "\n" +
                        "  # Event handlers are created and registered synchronously during ActorSystem\n" +
                        "  # start-up, and since they are actors, this timeout is used to bound the\n" +
                        "  # waiting time\n" +
                        "  event-handler-startup-timeout = 5s\n" +
                        "\n" +
                        "  # Log level used by the configured loggers (see \"event-handlers\") as soon\n" +
                        "  # as they have been started; before that, see \"stdout-loglevel\"\n" +
                        "  # Options: ERROR, WARNING, INFO, DEBUG\n" +
                        "  loglevel = \"INFO\"\n" +
                        "\n" +
                        "  # Log level for the very basic logger activated during AkkaApplication startup\n" +
                        "  # Options: ERROR, WARNING, INFO, DEBUG\n" +
                        "  stdout-loglevel = \"WARNING\"\n" +
                        "\n" +
                        "  # Log the complete configuration at INFO level when the actor system is started.\n" +
                        "  # This is useful when you are uncertain of what configuration is used.\n" +
                        "  log-config-on-start = off\n" +
                        "\n" +
                        "  # List FQCN of extensions which shall be loaded at actor system startup.\n" +
                        "  # Should be on the format: 'extensions = [\"foo\", \"bar\"]' etc.\n" +
                        "  # See the Akka Documentation for more info about Extensions\n" +
                        "  extensions = []\n" +
                        "\n" +
                        "  # Toggles whether the threads created by this ActorSystem should be daemons or not\n" +
                        "  daemonic = off\n" +
                        "\n" +
                        "  # JVM shutdown, System.exit(-1), in case of a fatal error, such as OutOfMemoryError\n" +
                        "  jvm-exit-on-fatal-error = on\n" +
                        "\n" +
                        "  actor {\n" +
                        "\n" +
                        "    provider = \"akka.actor.LocalActorRefProvider\"\n" +
                        "\n" +
                        "    # Timeout for ActorSystem.actorOf\n" +
                        "    creation-timeout = 20s\n" +
                        "\n" +
                        "    # frequency with which stopping actors are prodded in case they had to be\n" +
                        "    # removed from their parents\n" +
                        "    reaper-interval = 5s\n" +
                        "\n" +
                        "    # Serializes and deserializes (non-primitive) messages to ensure immutability,\n" +
                        "    # this is only intended for testing.\n" +
                        "    serialize-messages = off\n" +
                        "\n" +
                        "    # Serializes and deserializes creators (in Props) to ensure that they can be sent over the network,\n" +
                        "    # this is only intended for testing.\n" +
                        "    serialize-creators = off\n" +
                        "\n" +
                        "    typed {\n" +
                        "      # Default timeout for typed actor methods with non-void return type\n" +
                        "      timeout = 5s\n" +
                        "    }\n" +
                        "\n" +
                        "    deployment {\n" +
                        "\n" +
                        "      # deployment id pattern - on the format: /parent/child etc.\n" +
                        "      default {\n" +
                        "\n" +
                        "        # routing (load-balance) scheme to use\n" +
                        "        #     available: \"from-code\", \"round-robin\", \"random\", \"smallest-mailbox\", \"scatter-gather\", \"broadcast\"\n" +
                        "        #     or:        Fully qualified class name of the router class.\n" +
                        "        #                The router class must extend akka.routing.CustomRouterConfig and and have constructor\n" +
                        "        #                with com.typesafe.config.Config parameter.\n" +
                        "        #     default is \"from-code\";\n" +
                        "        # Whether or not an actor is transformed to a Router is decided in code only (Props.withRouter).\n" +
                        "        # The type of router can be overridden in the configuration; specifying \"from-code\" means\n" +
                        "        # that the values specified in the code shall be used.\n" +
                        "        # In case of routing, the actors to be routed to can be specified\n" +
                        "        # in several ways:\n" +
                        "        # - nr-of-instances: will create that many children\n" +
                        "        # - routees.paths: will look the paths up using actorFor and route to\n" +
                        "        #   them, i.e. will not create children\n" +
                        "        # - resizer: dynamically resizable number of routees as specified in resizer below\n" +
                        "        router = \"from-code\"\n" +
                        "\n" +
                        "        # number of children to create in case of a non-direct router; this setting\n" +
                        "        # is ignored if routees.paths is given\n" +
                        "        nr-of-instances = 1\n" +
                        "\n" +
                        "        # within is the timeout used for routers containing future calls\n" +
                        "        within = 5 seconds\n" +
                        "\n" +
                        "        routees {\n" +
                        "          # Alternatively to giving nr-of-instances you can specify the full\n" +
                        "          # paths of those actors which should be routed to. This setting takes\n" +
                        "          # precedence over nr-of-instances\n" +
                        "          paths = []\n" +
                        "        }\n" +
                        "\n" +
                        "        # Routers with dynamically resizable number of routees; this feature is enabled\n" +
                        "        # by including (parts of) this section in the deployment\n" +
                        "        resizer {\n" +
                        "\n" +
                        "          # The fewest number of routees the router should ever have.\n" +
                        "          lower-bound = 1\n" +
                        "\n" +
                        "          # The most number of routees the router should ever have.\n" +
                        "          # Must be greater than or equal to lower-bound.\n" +
                        "          upper-bound = 10\n" +
                        "\n" +
                        "          # Threshold to evaluate if routee is considered to be busy (under pressure).\n" +
                        "          # Implementation depends on this value (default is 1).\n" +
                        "          # 0:   number of routees currently processing a message.\n" +
                        "          # 1:   number of routees currently processing a message has\n" +
                        "          #      some messages in mailbox.\n" +
                        "          # > 1: number of routees with at least the configured pressure-threshold\n" +
                        "          #      messages in their mailbox. Note that estimating mailbox size of\n" +
                        "          #      default UnboundedMailbox is O(N) operation.\n" +
                        "          pressure-threshold = 1\n" +
                        "\n" +
                        "          # Percentage to increase capacity whenever all routees are busy.\n" +
                        "          # For example, 0.2 would increase 20% (rounded up), i.e. if current\n" +
                        "          # capacity is 6 it will request an increase of 2 more routees.\n" +
                        "          rampup-rate = 0.2\n" +
                        "\n" +
                        "          # Minimum fraction of busy routees before backing off.\n" +
                        "          # For example, if this is 0.3, then we'll remove some routees only when\n" +
                        "          # less than 30% of routees are busy, i.e. if current capacity is 10 and\n" +
                        "          # 3 are busy then the capacity is unchanged, but if 2 or less are busy\n" +
                        "          # the capacity is decreased.\n" +
                        "          # Use 0.0 or negative to avoid removal of routees.\n" +
                        "          backoff-threshold = 0.3\n" +
                        "\n" +
                        "          # Fraction of routees to be removed when the resizer reaches the\n" +
                        "          # backoffThreshold.\n" +
                        "          # For example, 0.1 would decrease 10% (rounded up), i.e. if current\n" +
                        "          # capacity is 9 it will request an decrease of 1 routee.\n" +
                        "          backoff-rate = 0.1\n" +
                        "\n" +
                        "          # When the resizer reduce the capacity the abandoned routee actors are stopped\n" +
                        "          # with PoisonPill after this delay. The reason for the delay is to give concurrent\n" +
                        "          # messages a chance to be placed in mailbox before sending PoisonPill.\n" +
                        "          # Use 0s to skip delay.\n" +
                        "          stop-delay = 1s\n" +
                        "\n" +
                        "          # Number of messages between resize operation.\n" +
                        "          # Use 1 to resize before each message.\n" +
                        "          messages-per-resize = 10\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "\n" +
                        "    default-dispatcher {\n" +
                        "      # Must be one of the following\n" +
                        "      # Dispatcher, (BalancingDispatcher, only valid when all actors using it are of\n" +
                        "      # the same type), PinnedDispatcher, or a FQCN to a class inheriting\n" +
                        "      # MessageDispatcherConfigurator with a constructor with\n" +
                        "      # com.typesafe.config.Config parameter and akka.dispatch.DispatcherPrerequisites\n" +
                        "      # parameters.\n" +
                        "      # PinnedDispatcher must be used toghether with executor=thread-pool-executor.\n" +
                        "      type = \"Dispatcher\"\n" +
                        "\n" +
                        "      # Which kind of ExecutorService to use for this dispatcher\n" +
                        "      # Valid options:\n" +
                        "      #               \"fork-join-executor\" requires a \"fork-join-executor\" section\n" +
                        "      #               \"thread-pool-executor\" requires a \"thread-pool-executor\" section\n" +
                        "      #               or\n" +
                        "      #               A FQCN of a class extending ExecutorServiceConfigurator\n" +
                        "      executor = \"fork-join-executor\"\n" +
                        "\n" +
                        "      # This will be used if you have set \"executor = \"fork-join-executor\"\"\n" +
                        "      fork-join-executor {\n" +
                        "        # Min number of threads to cap factor-based parallelism number to\n" +
                        "        parallelism-min = 8\n" +
                        "\n" +
                        "        # Parallelism (threads) ... ceil(available processors * factor)\n" +
                        "        parallelism-factor = 3.0\n" +
                        "\n" +
                        "        # Max number of threads to cap factor-based parallelism number to\n" +
                        "        parallelism-max = 64\n" +
                        "      }\n" +
                        "\n" +
                        "      # This will be used if you have set \"executor = \"thread-pool-executor\"\"\n" +
                        "      thread-pool-executor {\n" +
                        "        # Keep alive time for threads\n" +
                        "        keep-alive-time = 60s\n" +
                        "\n" +
                        "        # Min number of threads to cap factor-based core number to\n" +
                        "        core-pool-size-min = 8\n" +
                        "\n" +
                        "        # No of core threads ... ceil(available processors * factor)\n" +
                        "        core-pool-size-factor = 3.0\n" +
                        "\n" +
                        "        # Max number of threads to cap factor-based number to\n" +
                        "        core-pool-size-max = 64\n" +
                        "\n" +
                        "        # Hint: max-pool-size is only used for bounded task queues\n" +
                        "        # minimum number of threads to cap factor-based max number to\n" +
                        "        max-pool-size-min = 8\n" +
                        "\n" +
                        "        # Max no of threads ... ceil(available processors * factor)\n" +
                        "        max-pool-size-factor  = 3.0\n" +
                        "\n" +
                        "        # Max number of threads to cap factor-based max number to\n" +
                        "        max-pool-size-max = 64\n" +
                        "\n" +
                        "        # Specifies the bounded capacity of the task queue (< 1 == unbounded)\n" +
                        "        task-queue-size = -1\n" +
                        "\n" +
                        "        # Specifies which type of task queue will be used, can be \"array\" or\n" +
                        "        # \"linked\" (default)\n" +
                        "        task-queue-type = \"linked\"\n" +
                        "\n" +
                        "        # Allow core threads to time out\n" +
                        "        allow-core-timeout = on\n" +
                        "      }\n" +
                        "\n" +
                        "      # How long time the dispatcher will wait for new actors until it shuts down\n" +
                        "      shutdown-timeout = 1s\n" +
                        "\n" +
                        "      # Throughput defines the number of messages that are processed in a batch\n" +
                        "      # before the thread is returned to the pool. Set to 1 for as fair as possible.\n" +
                        "      throughput = 5\n" +
                        "\n" +
                        "      # Throughput deadline for Dispatcher, set to 0 or negative for no deadline\n" +
                        "      throughput-deadline-time = 0ms\n" +
                        "\n" +
                        "      # If negative (or zero) then an unbounded mailbox is used (default)\n" +
                        "      # If positive then a bounded mailbox is used and the capacity is set using the\n" +
                        "      # property\n" +
                        "      # NOTE: setting a mailbox to 'blocking' can be a bit dangerous, could lead to\n" +
                        "      # deadlock, use with care\n" +
                        "      # The following mailbox-push-timeout-time is only used for type=Dispatcher and\n" +
                        "      # only if mailbox-capacity > 0\n" +
                        "      mailbox-capacity = -1\n" +
                        "\n" +
                        "      # Specifies the timeout to add a new message to a mailbox that is full -\n" +
                        "      # negative number means infinite timeout. It is only used for type=Dispatcher\n" +
                        "      # and only if mailbox-capacity > 0\n" +
                        "      mailbox-push-timeout-time = 10s\n" +
                        "\n" +
                        "      # FQCN of the MailboxType, if not specified the default bounded or unbounded\n" +
                        "      # mailbox is used. The Class of the FQCN must have a constructor with\n" +
                        "      # (akka.actor.ActorSystem.Settings, com.typesafe.config.Config) parameters.\n" +
                        "      mailbox-type = \"\"\n" +
                        "\n" +
                        "      # For BalancingDispatcher: If the balancing dispatcher should attempt to\n" +
                        "      # schedule idle actors using the same dispatcher when a message comes in,\n" +
                        "      # and the dispatchers ExecutorService is not fully busy already.\n" +
                        "      attempt-teamwork = on\n" +
                        "\n" +
                        "      # For Actor with Stash: The default capacity of the stash.\n" +
                        "      # If negative (or zero) then an unbounded stash is used (default)\n" +
                        "      # If positive then a bounded stash is used and the capacity is set using the\n" +
                        "      # property\n" +
                        "      stash-capacity = -1\n" +
                        "    }\n" +
                        "\n" +
                        "    debug {\n" +
                        "      # enable function of Actor.loggable(), which is to log any received message at\n" +
                        "      # DEBUG level, see the “Testing Actor Systems” section of the Akka Documentation\n" +
                        "      # at http://akka.io/docs\n" +
                        "      receive = off\n" +
                        "\n" +
                        "      # enable DEBUG logging of all AutoReceiveMessages (Kill, PoisonPill and the like)\n" +
                        "      autoreceive = off\n" +
                        "\n" +
                        "      # enable DEBUG logging of actor lifecycle changes\n" +
                        "      lifecycle = off\n" +
                        "\n" +
                        "      # enable DEBUG logging of all LoggingFSMs for events, transitions and timers\n" +
                        "      fsm = off\n" +
                        "\n" +
                        "      # enable DEBUG logging of subscription changes on the eventStream\n" +
                        "      event-stream = off\n" +
                        "    }\n" +
                        "\n" +
                        "    # Entries for pluggable serializers and their bindings.\n" +
                        "    serializers {\n" +
                        "      java = \"akka.serialization.JavaSerializer\"\n" +
                        "    }\n" +
                        "\n" +
                        "    # Class to Serializer binding. You only need to specify the name of an interface\n" +
                        "    # or abstract base class of the messages. In case of ambiguity it is using the\n" +
                        "    # most specific configured class, or giving a warning and choosing the “first” one.\n" +
                        "    #\n" +
                        "    # To disable one of the default serializers, assign its class to \"none\", like\n" +
                        "    # \"java.io.Serializable\" = none\n" +
                        "    serialization-bindings {\n" +
                        "      \"java.io.Serializable\" = java\n" +
                        "    }\n" +
                        "  }\n" +
                        "\n" +
                        "  # Used to set the behavior of the scheduler.\n" +
                        "  # Changing the default values may change the system behavior drastically so make sure\n" +
                        "  # you know what you're doing! See the Scheduler section of the Akka documentation for more details.\n" +
                        "  scheduler {\n" +
                        "    # The HashedWheelTimer (HWT) implementation from Netty is used as the default scheduler\n" +
                        "    # in the system.\n" +
                        "    # HWT does not execute the scheduled tasks on exact time.\n" +
                        "    # It will, on every tick, check if there are any tasks behind the schedule and execute them.\n" +
                        "    # You can increase or decrease the accuracy of the execution timing by specifying smaller\n" +
                        "    # or larger tick duration.\n" +
                        "    # If you are scheduling a lot of tasks you should consider increasing the ticks per wheel.\n" +
                        "    # For more information see: http://www.jboss.org/netty/\n" +
                        "    tick-duration = 100ms\n" +
                        "    ticks-per-wheel = 512\n" +
                        "  }\n" +
                        "}";
        copyFileUsingStream(dest);
    }

    private static void copyFileUsingStream(File dest) throws IOException {

        OutputStream os = null;
        try {
            byte dataToWrite[] = applicationConf.getBytes();
            FileOutputStream out = new FileOutputStream(dest);
            out.write(dataToWrite);
            out.close();
        } finally {
            os.close();
        }
    }
}
