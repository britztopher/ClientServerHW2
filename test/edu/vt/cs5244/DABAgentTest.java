/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.vt.cs5244;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author christopherbritz
 */
public class DABAgentTest {
    
    private final DABClient client = new DABClient();
    
    public DABAgentTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws IOException {
        
        client.connect("dab.prof-oliva.com");
    }
    
    @After
    public void tearDown() throws IOException {
        System.setOut(null);
        System.setErr(null);
    }

    /**
     * Test of connect method, of class DABAgent.
     */
    @Test
    public void testConnect() {
        System.out.println("connect");
        String server = "dab.prof-oliva.com";
        DABAgent instance = new DABClient();
        boolean expResult = true;
        boolean result = instance.connect(server);
        assertEquals(expResult, result);
    }

    /**
     * Test of disconnect method, of class DABAgent.
     * @throws java.io.IOException
     */
    @Test
    public void testDisconnect() throws IOException {
            
        client.disconnect();

    }
    
    @Test
    public void testReadAndWrite() throws IOException {      
        client.sendCommand("INIT!\n");
       
        
    }
    
    @Test
    public void testInit() throws IOException {      
        client.init(3);
        client.getSize();
 
    }
    
 
}
