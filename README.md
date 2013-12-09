Sirius-Reference-App
A simple reference application that helps developers get started with using the Sirius library


Getting Started Tutorial

This tutorial used code snippets from a simple application that uses the Sirius library to provide
a simple destribute and consistent data store. We will first walk through the steps to write a Sirus powered application.

When developeing with Sirius there are four blocks that you need. A Sirius representation, some class
that configures what will be your Sirius Implimentation. A RequestHandler class that extends the Sirius
RequestHandle trait/interface, overrinding the handleGet, handlePut and handleDelete method. A controller
class that determines when and what data gets written to or read from Sirius. And finally a implimentation
of some in memory data store.

Configuring Sirius: Here we configure what the cluster will look like, how many, how often they message
each other etc.



    private String defaultWriteAheadLog;
    private String clusterConfig;
    private FullRequestHandler requestHandler;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());


    public SiriusImpl startSirius(int port, String customWriteAheadLog){......}
    
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

    private static FullRequestHandler createRequestHandler(){......}

    private static String createClusterConfig(String location, int port){......}

    private static String createSiriusLog(String location){......}

    private void isBooted(SiriusImpl siriusImpl , Long timeout){......}

    public void shutdown(SiriusImpl siriusImpl){......}


Implementing a RequestHandler: Here we are only interested in the handlePut and handleGet. When you 
issure and enqueuePut the underlying Sirius library also fires the corrsponding RequestHandler’s handlePut
method. when you issue an enqueueGet the Sirius library then fires the corresponding RequestHandler’s 
handleGet method.

    public FullRequestHandler(){
        logger = LoggerFactory.getLogger(this.getClass());
    }
    
    public SiriusResult handleGet(String key){
        logger.trace("Handling a Get for:", key);
        if (InMemoryDataStore.IMS.getContainer(key) != null){
            return SiriusResult.some(InMemoryDataStore.IMS.getContainer(key));
        }
        else{
            System.out.println("Returning: NONE ");
            return SiriusResult.none();
        }
    }

   
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


Storing Data in Sirius: Storing data in Sirius is simple. You can do an enqueueGet, enqueuePut or enqueueDelete.

    String container;

    private final Sirius sirius = StartServer.SIRIUS;

    public ContainerController(UriInfo uriInfo, Request request, String container ) {......}
    
    @GET
    public Container getContainer(@QueryParam("search") String search)throws InterruptedException, ExecutionException {

        Future<SiriusResult> future = sirius.enqueueGet(container);
        SiriusResult result = future.get();

       
    }

    @PUT
    public Response putContainer()throws InterruptedException, ExecutionException {
    
        Future < SiriusResult > future = sirius.enqueuePut(c.getName(), bytes);
        future.get();
    }
    ......
    ......
    

Starting the Server:

    protected static Logger logger = LoggerFactory.getLogger(StartServer.class);
    public static final  int DEFAULT_SERVER_PORT = 9998;
    public static final  int DEFAULT_SIRIUS_PORT = 42289;
    public static final URI BASE_URI = UriBuilder.fromUri("http://localhost/storage").port(DEFAULT_SERVER_PORT).build();
    public static int CUSTOM_SERVER_PORT;
    public static int CUSTOM_SIRIUS_PORT;
    public static URI CUSTOM_BASE_URI;
    public static String CUSTOM_WRITE_AHEAD_LOG;
    public static  SiriusImpl SIRIUS;

    public static void main(String[] args) throws IOException {
        ......

            CUSTOM_BASE_URI = UriBuilder.fromUri("http://localhost/storage").port(CUSTOM_SERVER_PORT).build();
            SelectorThread threadSelector = startServer(CUSTOM_SIRIUS_PORT, CUSTOM_WRITE_AHEAD_LOG, CUSTOM_BASE_URI);
            ......
        }
        
    
    protected static SelectorThread startServer(int siriusPort, String siriusLog, URI BASE_URI) throws IOException {
        final Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "com.comcast.xfinity.sirius.refapplication.endpoints");

        SIRIUS = new SiriusImplementation().startSirius(siriusPort, siriusLog);

        logger.info("Starting Service with the Jersey Java reference container");
        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(BASE_URI, initParams);
        return threadSelector;
    }
