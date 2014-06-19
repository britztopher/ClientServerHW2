/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.vt.cs5244;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


//TODO-I NEED TO remove printstacktraces and refactor exception handling
/**
 *
 * @author christopherbritz
 */
public class DABClient implements DABAgent{

    Socket connection; 
    
    //TODO-maybe make this an enum?
    private static final String GET_SIZE = "SIZE?";
    private static final String INIT = "INIT!";
    private static final String DRAW = "DRAW!"; //INT, INT, EDGE
    private static final String EDGES = "EDGES?";//INT, INT
    private static final String OWNER = "OWNER?";//INT, INT
    private static final String TURN = "TURN?";//RETURNS PLAYER
    private static final String SCORE = "SCORE?";//PLAYER
    private static final String QUIT = "QUIT!";

    private static  BufferedReader in;
    private static PrintWriter pw;
    
    
    @Override
    public boolean connect(String server) {
        
        boolean isConnected = false;
        
        try {
                this.connection = new Socket(server, 5244);
                
                pw = new PrintWriter(connection.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                this.readResponse();
                
                isConnected = true;

            } catch (IOException ex) {
                throw new DABClientException();
            }
        
        return isConnected;
    }

    @Override
    public void disconnect() {
        if(this.connection != null){
            
            while(!this.connection.isClosed()){
                
                try {
                    this.connection.close();
                    System.out.print("Connection Closed");
                } catch (IOException ex) {
                    
                }
            }
        }
    }

    @Override
    public void init(int size) {
        
        this.sendCommand(INIT);
        this.sendInt(size);
        this.readAck();

    }

    @Override
    public int getSize() {
        
        int size = 0;

        this.sendCommand(GET_SIZE);
        this.readAck();
        size = this.getInt();
        
        return size;
    }

    @Override
    public Set<Edge> getEdgesAt(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Player getOwnerAt(int row, int col) {
        this.sendCommand(OWNER);
        this.sendInt(row);
        this.sendInt(col);
        this.readAck();
        return this.getPlayer();
    }

    @Override
    public boolean drawEdge(int row, int col, Edge edge) {
       
        this.sendCommand(DRAW);
        this.sendInt(row);
        this.sendInt(col);
        this.sendEdge(edge);
        this.readAck();
        return this.getBln();
    }

    @Override
    public Map<Player, Integer> getScores() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Player getTurn() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String readResponse(){

        String line;
        
        try {
            line = in.readLine();
        } catch (IOException ex) {
            throw new DABClientException();
        }
        
        //FIXME-Need to get rid of debug statements
        debug(line);
     
        return line;
    }
    
    public void sendCommand(Object command){       
       
        String resp;
        
        pw.println(command);
        resp = this.readResponse();
        
        if(!resp.startsWith("OK.") && !resp.startsWith("GOT")){
           throw new DABClientException();
        }else if(resp.startsWith("DEX")){
            throw new DABException();
        }
 
    }
    
    public void sendInt(Integer num){
        
        this.sendCommand(num);
         
        
    }
    
    public void readAck(){
        
        String ackMsg; 
        ackMsg = this.readResponse();
        
        if(!ackMsg.startsWith("ACK!")){
            throw new DABException();
        }
    }
    

    private int getInt(){
        
        int myInt; 
        
        try{
            myInt = Integer.parseInt(this.readResponse());
        }catch(NumberFormatException ex){
            throw new DABClientException();
        }
        
        return myInt;
        
    }
    
    private static void debug(String msg){
    
        System.out.println("Client: " + msg);
    }

    private void sendEdge(Edge edge) {
        
        this.sendCommand(edge);
    }

    private boolean getBln() {
       
        String resp = this.readResponse();
        boolean wasDrawn = false; 
        
        if(resp.equalsIgnoreCase("true")){
            wasDrawn = true;
        }
        
        return wasDrawn;
    }

    private Player getPlayer() {
        
        String response = this.readResponse();
        Player player;
        
        if(response.equals(Player.ONE)){
            player = Player.ONE;
        }else if(response.equalsIgnoreCase("None")){
            player = null;
        }else{
            player = Player.TWO;
        }
        
        return player;
    }
    
}
