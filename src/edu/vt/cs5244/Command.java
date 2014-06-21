/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.vt.cs5244;

/**
 *
 * @author christopherbritz
 */
public enum Command {
        SIZE("SIZE?"),
        INIT("INIT!"),
        DRAW("DRAW!"),
        EDGES("EDGES?"),
        OWNER("OWNER?"), 
        TURN("TURN?"), 
        SCORE("SCORE?"),
        QUIT("QUIT!");
    
    private final String command;
    
    Command(String command) {
        this.command = command;
    }
    
    public String getValue(){
        return this.command;       
    }
                
 
}
