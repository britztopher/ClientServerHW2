package edu.vt.cs5244;

import java.io.*;
import java.net.*;

/**
 * This class implements the main application logic for a multi-threaded server.
 * It spawns new worker threads as needed, one thread per client connection.
 * 
 */
public class DABServer {

    /**
     * Creates a server socket to listen for (and accept) new client connection requests.
     * This main thread keeps running indefinitely, until stopped externally 
     * by the execution environment.
     */
    public static void main(String[] args) throws IOException {

        // Bind a server socket to TCP port 5244 (as DDP requires) 
        // to listen for new connection requests from clients.
        ServerSocket serverSocket = new ServerSocket(5244);

        // Stay in this loop forever
        while (true) {

            // Accept the next connection request that arrives
            Socket clientSocket = serverSocket.accept();

            // Instantiate a new worker object to handle this client
            Runnable handler = new DABServerWorker(clientSocket);

            // Create a new Thread environment for the handler
            Thread handlerThread = new Thread(handler);

            // Start the new thread, indirectly invoking its run() method
            handlerThread.start();
            
            // A new instance of the worker is now executing its run() method in
            // a dedicated execution thread, completely independent of (and
            // concurrent with) this main loop as well as any other threads that
            // have been (or will be) spawned to handle other clients.

        }
    }
}