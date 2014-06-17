/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.vt.cs5244;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    private static final String CHAR_RTN = "\n";
    private static final String GET_SIZE = "SIZE?";
    private static final String INIT = "INIT!";
//    private static final String INIT = "INIT!";
    
    
    @Override
    public boolean connect(String server) {
        
        boolean isConnected = false;
        
        try {
                this.connection = new Socket(server, 5244);
//                BufferedOutputStream bos = 
//                        new BufferedOutputStream(connection.getOutputStream());
//
//            /** Instantiate an OutputStreamWriter object with the optional character
//              * encoding.
//             */
//                OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
//
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
        
        try {
            this.sendCommand(INIT);
            this.sendInt(size);
            this.readAck();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getSize() {
        
        int size = 0;
        try {
            this.sendCommand(GET_SIZE);
            this.readAck();
            size = this.getInt();
            
           
            
        } catch (IOException ex) {
           ex.printStackTrace();
        }

        
        return size;
        
    }

    @Override
    public Set<Edge> getEdgesAt(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Player getOwnerAt(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean drawEdge(int i, int i1, Edge edge) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Player, Integer> getScores() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Player getTurn() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Object readResponse() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = null;
        
        line = in.readLine();
        
        System.out.println(line);  
        
        return line;
    }
    
    public void sendCommand(Object command) throws IOException{
        
        OutputStream output = connection.getOutputStream();
        PrintWriter pw = new PrintWriter(output);
        
        pw.println(command);
        pw.flush();
        
        this.readResponse();
        
    }
    
    public void sendInt(Integer num) throws IOException{
        
        this.sendCommand(num);
    }
    
    public void readAck() throws IOException{
        this.readResponse();

    }
    

    private int getInt() throws IOException {
        this.readResponse();
        
        return 3;
        
    }
    
}
