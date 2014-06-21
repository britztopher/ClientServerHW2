package edu.vt.cs5244;

import java.io.*;
import java.net.*;

/**
 * This is the beginning of an implementation, which you can use as a starting point.
 * Please note that this class is *not* DDP compliant as provided.
 * 
 */
public class DABServerWorker implements Runnable {

    private final BufferedReader clReader;
    private final Socket clSocket;
    private final PrintWriter clWriter;
    private DABEngine theDAB;

    public DABServerWorker(Socket cSock) throws IOException {
        
        // Keep our Socket dedicated to this instance of the worker.
        clSocket = cSock;
        
        // We'll wrap the I/O streams within more convenient Reader and Writer objects,
        clWriter = new PrintWriter(clSocket.getOutputStream(), true);
        clReader = new BufferedReader(new InputStreamReader(clSocket.getInputStream()));

        // Finally we instantiate a new DABEngine dedicated to this client.
        // Note that this engine is instantiated once per worker instance, thus once per thread, 
        // and so it is not accessibly by any of the other concurrent threads.
        // PENDING: You must uncomment the following line
        //theDAB = new HW1_DAB();
    }

    /**
     * Helper method to read a single line from the client.
     *
     * @return String representing the next line read from the client
     * @throws DABServerIOException if an IO error occurred while reading the line
     */
    private String readFromClient() {
        try {
            // Attempt to read the next line from the stream.
            // This may throw an IOException due to a TCP connection failure.
            String temp = clReader.readLine(); 

            // If the readLine() returned null, it means the "end" of the stream was reached.
            // That means the client cleanly (but prematurely) closed its socket.
            // It's unexpected because we shouldn't ever attempt to read a line unless
            // we're expecting something from the client, since the DDP is strictly lock-step.
            // We'll just handle this case exactly as any other IOException.
            if (temp == null) {
                throw new IOException();
            }
            
            // Otherwise we got a valid value; let's return it.
            return temp;
            
        } catch (IOException ioe) {
            // For any other IOException, the connection is already broken; 
            // just exit gracefully via DABServerIOException.
            throw new DABServerIOException();
        }
    }

    @Override
    public void run() {
        try {

            // Now we'll welcome the client to our server 
            // PENDING: DDP requires this message to have a specific format; the line below is non-compliant!
            clWriter.println("Welcome to the DDP server!");

            // We'll use this to flag that we need to exit the while-loop.
            boolean done = false;

            // We'll keep servicing this client until something sets our done flag
            while (!done) {
                try {
                    // Let's use our helper method to populate the command string (which is never null)
                    String command = readFromClient();

                    switch(command) {
                        case "INIT!":
                            // PENDING implementation
                            break;
                        case "SIZE?":
                            // PENDING implementation
                            break;
                        case "EDGES?":
                            // PENDING implementation
                            break;
                        case "OWNER?":
                            // PENDING implementation
                            break;
                        case "SCORE?":
                            // PENDING implementation
                            break;
                        case "TURN?":
                            // PENDING implementation
                            break;
                        case "DRAW!":
                            // PENDING implementation
                            break;
                        case "QUIT!":
                            // PENDING implementation
                            // To disconnect, we'll flag that we're done the while-loop
                            done = true;
                            break;
                        default:
                            // PENDING implementation
                            break;
                    }
                } catch (DABServerParamException dpe) {
                    // This will catch any exceptions we throw elsewhere, 
                    // which indicate bad parameters.  
                    // We need to send a particular DDP response in this case.
                    // PENDING implementation
                } catch (DABException de) {
                    // This will catch any DABException thrown by the HW1_DAB.
                    // We need to send a particular DDP response in this case.
                    // PENDING implementation
                }
            }
            
        // To get here normally, we must have set done=true to exit the while-loop,
        // so there's nothing more to do here.

        } catch (DABServerIOException dse) {
            // How we got here:
            // (1) The client "hung up" on us cleanly but prematurely in readFromClient()
            // (2) The client uncleanly/abruptly shutdown the connection
            // (3) TCP detected that network between here and the client has broken.
            // Basically our TCP connection is gone, so there's nothing more to do.
        }

        // No matter how we got here, we should close our streams and the Socket itself.
        try {
            clWriter.close();
            clReader.close();
            clSocket.close();
        } catch (IOException ioe) {
            // If we couldn't close everything programmatically, the platform will clean up for us.
        }
        
        // The end of the run() method lets this thread exit gracefully, thus discarding
        // this worker instance (and our client's HW1_DAB instance along with it).
    }
}