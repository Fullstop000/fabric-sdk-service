package com.yunphant.grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * CommonRPCServer
 */
public class CommonRPCServer {

    private static final Logger logger = LoggerFactory.getLogger(CommonRPCServer.class);

    /** The server obj */
    private final Server server;
    /** The server port */
    private final int port;


    /**
     * Instantiates a new Common rpc server.
     *
     * @param port    the port
     * @param service the service
     */
    public CommonRPCServer(int port , BindableService service){
        this.port = port;
        this.server = ServerBuilder.forPort(port).addService(service).build();
    }

    /**
     * Start serving requests.  @throws IOException the io exception
     */
    protected void start() throws IOException {
        server.start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may has been reset by its JVM shutdown hook.
            logger.info("*** shutting down gRPC server since JVM is shutting down");
            CommonRPCServer.this.stop();
            logger.info("*** server shut down");
        }));
    }

    /** Stop serving requests and shutdown resources. */
    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     *
     * @throws InterruptedException the interrupted exception
     */
    protected void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
