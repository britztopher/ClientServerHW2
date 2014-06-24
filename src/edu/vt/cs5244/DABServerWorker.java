package edu.vt.cs5244;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * This is the beginning of an implementation, which you can use as a starting point.
 * Please note that this class is *not* DDP compliant as provided.
 * 
 */
public class DABServerWorker implements Runnable {

    private final BufferedReader clReader;
    private final Socket clSocket;
    private final PrintWriter clWriter;
    
    private static final String ACCEPTED = "accepted";
    private static final String ACK = "ack";
    private static final String BADPARAM = "badParam";
    private static final String BADCMD = "badCmd";
    private static final String VALID = "valid";
    private static final String EXCEPTION = "exception";
    private static final String VALUE = "value";
  
    private final DABEngine theDAB;
    

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
        theDAB = new HW1_DAB();
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
            clWriter.println("OK. WELCOME!");

            // We'll use this to flag that we need to exit the while-loop.
            boolean done = false;

            // We'll keep servicing this client until something sets our done flag
            while (!done) {
                try {
                    // Let's use our helper method to populate the command string (which is never null)
                    String command = readFromClient();

                    switch(command) {
                        case "INIT!":
                            this.writeResp(ACCEPTED);
                            int boardSize = this.getInt();
                            theDAB.init(boardSize);
                            this.writeResp(ACK);
                            break;
                        case "SIZE?":
                            this.writeResp(ACCEPTED);
                            this.writeResp(ACK);
                            int size = theDAB.getSize();
                            writeResp(VALUE, String.valueOf(size));
                            break;
                        case "EDGES?":
                            this.writeResp(ACCEPTED);
                            int edgeRow = this.getInt();
                            int edgeCol = this.getInt();
                            Set<Edge> theEdges = theDAB.getEdgesAt(edgeRow, edgeCol);
                            if(theEdges.isEmpty()){
                                writeResp(VALUE, 0);
                            }else{
                                for(Edge edge: theEdges){
                                    this.writeResp(VALUE, this.edgeToString(edge));
                                }
                            }
                            
                            this.writeResp(ACK);
                            break;
                        case "OWNER?":
                            this.writeResp(ACCEPTED);
                            int ownerRow = this.getInt();
                            int ownerCol = this.getInt();
                            Player owner = theDAB.getOwnerAt(ownerRow, ownerCol);
                            writeResp(VALUE, owner);
                            break;
                        case "SCORE?":
                            this.writeResp(ACCEPTED);
                            Player player = getPlayer();
                            Map<Player, Integer> scoreMap = theDAB.getScores();
                            writeResp(VALUE, scoreMap.get(player));
                            writeResp(ACK);
                            break;
                        case "TURN?":
                            this.writeResp(ACCEPTED);
                            Player turn = theDAB.getTurn();
                            writeResp(VALUE, turn);
                            writeResp(ACK);
                            break;
                        case "DRAW!":
                            writeResp(ACCEPTED);
                            int row = this.getInt();
                            int col = this.getInt();
                            Edge drawnEdge = this.parseEdge(); 
                            boolean respVal = theDAB.drawEdge(row, col, drawnEdge);
                            writeResp(VALUE,respVal); 
                            writeResp(ACK);
                            break;
                        case "QUIT!":
                            writeResp(ACCEPTED);
                            // To disconnect, we'll flag that we're done the while-loop
                            done = true;
                            break;
                        default:
                           
                            
                            break;
                    }
                } catch (DABServerParamException dpe) {
                    // This will catch any exceptions we throw elsewhere, 
                    // which indicate bad parameters.  
                    // We need to send a particular DDP response in this case.
                    
                    this.writeResp(BADPARAM);
                    
                } catch (DABException de) {
                    // This will catch any DABException thrown by the HW1_DAB.
                    // We need to send a particular DDP response in this case.
                    this.writeResp(EXCEPTION);
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

    private void writeResp(Object... args) {
        
            switch((String)args[0]){
                
                case ACCEPTED:{
                    clWriter.println("OK. COMMAND ACCEPTED.");
                    break;
                }
                case BADCMD:{
                    clWriter.println("NO. COMMAND NOT RECOGNIZED.");
                    break;
                }
                case VALID:{
                    clWriter.println("GOT VALID PARAMETER.");
                    break;
                }
                case BADPARAM:{
                    clWriter.println("BAD PARAMETER NOT VALID.");
                    break;
                }
                case ACK:{
                    clWriter.println("ACK!");
                    break;
                }
                case VALUE:{
                    clWriter.println(args[1]);
                    break;
                }
                case EXCEPTION:{
                    clWriter.println("DEX!");
                    break;
                }
                
            }
    }
    
    private String readRequest(){

        String line;
        
        try {
            line = clReader.readLine();
        } catch (IOException ex) {
            throw new DABClientException();
        }
        
        //FIXME: Need to get rid of debug statements
        debug(line);
     
        return line;
    }

    private String parsePlyr(Player player) {
        
        String plyr;
        
        if(player == Player.ONE){
            plyr = "ONE";
        }else{
            plyr = "TWO";
        }
        
        return plyr;
    }

    private Edge parseEdge() {
        
        Edge thisEdge = null;
        String edgeReq = this.readRequest();
        
        switch(edgeReq){
            case "TOP":{
                thisEdge = Edge.TOP;
                break;
            }
                
            case "BOTTOM":{
                thisEdge = Edge.BOTTOM;
                break;
            }
                
            case "LEFT":{
                thisEdge = Edge.LEFT;
                break;
            }
                
            case "RIGHT":{
                thisEdge = Edge.RIGHT;
                break;
            }
            default:{
                writeResp(BADPARAM);
            }
        }
        
        return thisEdge;
    }
    
        private String edgeToString(Edge edge) {
        
        String thisEdge = null;
           
        if(edge == Edge.TOP){
            thisEdge = "TOP";
        }else if(edge == Edge.BOTTOM){
            thisEdge = "BOTTOM";
        }else if(edge == Edge.LEFT){
            thisEdge = "LEFT";
        }else if(edge == Edge.RIGHT ){
            thisEdge = "RIGHT";
        }
       
        return thisEdge;
        
    }
        
    private String getStringValue(int value){
        
        String strValue = "";
        
        try{
            strValue = String.valueOf(value);
        }catch(IllegalArgumentException iae){
            writeResp("dex");
        }
            
        return strValue;
    }
    
    private int getInt(){
        
        int myInt; 
        
        try{
            myInt = Integer.parseInt(this.readRequest());
        }catch(NumberFormatException ex){
            throw new DABClientException();
        }
        
        writeResp(VALID);
        
        return myInt;
        
    }
    
    private Player getPlayer() {
        
        String response = this.readRequest();
        Player player;
        
        if(response.equals("ONE")){
            player = Player.ONE;
        }else if(response.equals("NONE")){
            player = null;
        }else{
            player = Player.TWO;
        }
        
        return player;
    }
    
    private static void debug(String msg){
    
        System.out.println("Server: " + msg);
    }
}