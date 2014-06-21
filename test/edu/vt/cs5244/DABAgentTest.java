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
import java.util.Map;
import java.util.Set;
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
        client.disconnect();
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
    public void testInit() throws IOException { 
        client.init(4);
        assertEquals(client.getSize(), 4);
       
    }
    
    @Test
    public void testDraw() throws IOException { 
        client.init(4);
        
        client.drawEdge(1, 1, Edge.TOP); //ONE
        client.drawEdge(1, 1, Edge.BOTTOM);//2
        client.drawEdge(1, 1, Edge.LEFT);//1
        client.drawEdge(1, 1, Edge.RIGHT);//2
       
        assertEquals(Player.TWO, client.getOwnerAt(1, 1));
 
    }
    
    
     @Test
    public void testTurn() throws IOException { 
        client.init(4);
        
        Player first = client.getTurn();
        assertEquals("First player must be ONE", Player.ONE, first);
        client.drawEdge(0, 0, Edge.TOP);
        Player next = client.getTurn();
        assertEquals("Second player must be TWO", Player.TWO, next);
        
    }
    
    @Test
    public void testScores() throws IOException { 
        client.init(4);
        
        client.drawEdge(1, 1, Edge.TOP); //ONE
        client.drawEdge(1, 1, Edge.BOTTOM);//2
        client.drawEdge(1, 1, Edge.LEFT);//1
        client.drawEdge(1, 1, Edge.RIGHT);//2
        
        Map<Player, Integer> testMap = client.getScores();
        int score = testMap.get(Player.TWO);
        
        assertEquals("Second player must have greater score",  score, 1);
        
        score = testMap.get(Player.ONE);
        assertEquals("First player must have score of 0",  score, 0);
        
    }
    
    @Test
    public void testQuit() throws IOException { 
        client.init(4);
        
        client.drawEdge(1, 1, Edge.TOP); //ONE
        client.drawEdge(1, 1, Edge.BOTTOM);//2
        client.drawEdge(1, 1, Edge.LEFT);//1
        client.drawEdge(1, 1, Edge.RIGHT);//2
        
        client.quitGame();
        
    }
    
    @Test
    public void testGetEdgesAt() {
        client.init(2);
        
        client.drawEdge(0, 0, Edge.BOTTOM);
        client.drawEdge(0, 0, Edge.LEFT);
        client.drawEdge(0, 0, Edge.TOP);
        
        Set<Edge> three = client.getEdgesAt(0, 0);
        assertTrue("Drawn edge must appear in getEdgesAt", three.contains(Edge.BOTTOM));
        assertTrue("Drawn edge must appear in getEdgesAt", three.contains(Edge.LEFT));
        assertTrue("Drawn edge must appear in getEdgesAt", three.contains(Edge.TOP));
        assertFalse("Undrawn edge must not appear in getEdgesAt", three.contains(Edge.RIGHT));

        client.drawEdge(0, 0, Edge.RIGHT);
        assertEquals("New edge must appear in new getEdgesAt", 4, client.getEdgesAt(0, 0).size());
        assertFalse("New edge must not appear in previous getEdgesAt", three.contains(Edge.RIGHT));
        
        assertTrue("Shared edge must appear in getEdgesAt of neighbor", client.getEdgesAt(1, 0).contains(Edge.TOP));
        assertTrue("Shared edge must appear in getEdgesAt of neighbor", client.getEdgesAt(0, 1).contains(Edge.LEFT));
    }
    
//    @Test
//    public void testExceptions() {
//        try {
//            new HW1_DAB().getTurn();
//            fail("Getting turn of non-initialized game must throw DABException");
//        } catch (Exception e) {
//            if (!(e instanceof DABException)) {
//                fail("Wrong exception thrown getting turn of non-initialized game");
//            }
//        }
//        try {
//            client.drawEdge(0, -1, Edge.TOP);
//            fail("Drawing at invalid location must throw DABException");
//        } catch (Exception e) {
//            if (!(e instanceof DABException)) {
//                fail("Wrong exception thrown drawing at invalid location");
//            }
//        }
//        try {
//            client.drawEdge(0, 0, null);
//            fail("Drawing null edge must throw DABException");
//        } catch (Exception e) {
//            if (!(e instanceof DABException)) {
//                fail("Wrong exception thrown drawing null edge");
//            }
//        }
//        try {
//            client.getEdgesAt(4, 0);
//            fail("Getting edge at invalid location must throw DABException");
//        } catch (Exception e) {
//            if (!(e instanceof DABException)) {
//                fail("Wrong exception thrown getting edge at invalid location");
//            }
//        }
//        try {
//            client.init(1);
//            fail("Creating board grid less than size of 2 must throw DABException");
//        } catch (Exception e) {
//            if (!(e instanceof DABException)) {
//                fail("Wrong exception thrown getting edge at invalid location");
//            }
//        }
//        try {
//            client.init(2);
//            
//            client.drawEdge(0, 0, Edge.TOP);//ONE
//            client.drawEdge(0, 0, Edge.BOTTOM);//TWO
//            client.drawEdge(0, 0, Edge.LEFT);//ONE
//            client.drawEdge(0, 1, Edge.TOP);//TWO
//            client.drawEdge(0, 1, Edge.BOTTOM);//ONE
//            client.drawEdge(0, 1, Edge.RIGHT);//TWO
//            client.drawEdge(0, 0, Edge.RIGHT);//ONE
//            client.drawEdge(1, 0, Edge.TOP);//ONE
//            client.drawEdge(1, 0, Edge.BOTTOM);//TWO
//            client.drawEdge(1, 0, Edge.LEFT);//ONE
//            client.drawEdge(1, 1, Edge.TOP);//TWO
//            client.drawEdge(1, 1, Edge.BOTTOM);//ONE
//            client.drawEdge(1, 1, Edge.RIGHT);//TWO
//            client.drawEdge(1, 0, Edge.RIGHT);//ONE
//            client.drawEdge(0, 0, Edge.TOP); //draw an edge after game is over
//            
//            
//            fail("When game is over game must throw DABException");
//        } catch (Exception e) {
//            if (!(e instanceof DABException)) {
//                fail("Wrong exception thrown getting edge at invalid location");
//            }
//        }
//        
//    }
}
