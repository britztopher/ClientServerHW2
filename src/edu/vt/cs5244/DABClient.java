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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


//TODO-I NEED TO remove printstacktraces and refactor exception handling
/**
 *
 * @author christopherbritz
 */
public class DABClient implements DABAgent{

    private Socket connection;     
    //using an arraylist because we only have a couple of commands to loop 
    //through and a hashmap would be overkill
    private final ArrayList<String> pcrList = new ArrayList<>();
    
    private static  BufferedReader in;
    private static PrintWriter pw;
    
    public DABClient(){
        pcrList.add("ACK,");
        pcrList.add("GOT.");
    }
    
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
                    System.out.println("Connection Closed");
                } catch (IOException ex) {
                    
                }
            }
        }
    }

    @Override
    public void init(int size) {

        this.sendCommand(Command.INIT.getValue());
        this.sendInt(size);
        this.readAck();

    }

    @Override
    public int getSize() {
        
        int size = 0;

        this.sendCommand(Command.SIZE.getValue());
        this.readAck();
        size = this.getInt();
        
        return size;
    }

    @Override
    public Set<Edge> getEdgesAt(int row, int col) {

        Set<Edge> edgeSet = new HashSet<>();
        
        this.sendCommand(Command.EDGES.getValue());
        this.sendInt(row);
        this.sendInt(col);
        this.readAck();

        //get the number of edges so I can know how many iterations I need to 
        //make to getEdge
        int numOfEdges = this.getInt();
        
        if(numOfEdges>4){
            throw new DABClientException();
        }
       
        for(int i = 0; i < numOfEdges; i++){
            
            Edge edge = this.getEdge();
            
            edgeSet.add(edge);
            
        }
        
        return edgeSet;
    }

    @Override
    public Player getOwnerAt(int row, int col) {
        this.sendCommand(Command.OWNER.getValue());
        this.sendInt(row);
        this.sendInt(col);
        this.readAck();
        return this.getPlayer();
    }

    @Override
    public boolean drawEdge(int row, int col, Edge edge) {
       
        this.sendCommand(Command.DRAW.getValue());
        this.sendInt(row);
        this.sendInt(col);
        this.sendEdge(edge);
        this.readAck();
        return this.getBln();
    }

    @Override
    public Map<Player, Integer> getScores() {
       
        Map<Player, Integer> scoreMap = new HashMap<>();
        
        this.sendCommand(Command.SCORE.getValue());
        this.sendCommand(Player.ONE);
        this.readAck();
        scoreMap.put(Player.ONE, this.getInt());
        
        this.sendCommand(Command.SCORE.getValue());
        this.sendCommand(Player.TWO);
        this.readAck();
        scoreMap.put(Player.TWO, this.getInt());
        
        return scoreMap;
    }

    @Override
    public Player getTurn() {
        this.sendCommand(Command.TURN.getValue());
        this.readAck();
        return this.getPlayer();
    }
    
    @Override
    public void quitGame(){
        this.sendCommand(Command.QUIT.getValue());
        this.disconnect();
    }
    
    
    private String readResponse(){

        String line;
        
        try {
            line = in.readLine().toUpperCase();
        } catch (IOException ex) {
            throw new DABClientException();
        }
        
        //FIXME-Need to get rid of debug statements
        debug(line);
     
        return line;
    }
    
    private void sendCommand(Object command){       
       
        String resp;
        
        pw.println(command);
        resp = this.readResponse();
        
        if(resp == null || (!resp.startsWith("OK.") && !resp.startsWith("GOT"))){ 
            throw new DABClientException();
        }
 
    }
    
    private void sendInt(Integer num){
        
        this.sendCommand(num);
        
    }
    
    private void readAck(){
        
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
        
        switch(resp){
            case "TRUE":{
                wasDrawn = true;
                break;
            }
            case "FALSE":{
                wasDrawn = false;
            }
            
            default:{
                throw new DABClientException();
            }
            
        }
        
        return wasDrawn;
    }

    private Player getPlayer() {
        
        String response = this.readResponse();
        Player player;
        
        switch (response) {
            case "ONE":
                player = Player.ONE;
                break;
            case "NONE":
                player = null;
                break;
            default:
                player = Player.TWO;
                break;
        }
        
        return player;
    }

    private Edge getEdge() {
        
        String res = this.readResponse();
        Edge edge = null;
        
        switch(res){
            
            case "LEFT":{
                edge = Edge.LEFT;
                break;
            }
            
            case "RIGHT":{
                edge = Edge.RIGHT;
                break;
            }
            
            case "TOP":{
                edge = Edge.TOP;
                break;
            }
            
            case "BOTTOM":{
                edge = Edge.BOTTOM;
                break;
            }
            default:{
                throw new DABClientException();
            }
        }
        
        return edge;
    }
    
}
