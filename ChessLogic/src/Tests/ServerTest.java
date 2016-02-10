package Tests;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.file.*;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Test;

import Game.Move;
import Networking.OpCode;
import Networking.Packet;
import Server.Server;

public class ServerTest {

    private final static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final static ByteArrayOutputStream errContent = new ByteArrayOutputStream();


    static Server s;
    static Packet p;
    static FileOutputStream out;
    @BeforeClass
    public static void setUp() throws Exception {
        s = new Server();
        p = new Packet(OpCode.JoinQueue, -1, null);
        out = new FileOutputStream("outputTest.dat");
        
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    
    @Test
    public void processPacketJoinTest() throws IOException {
        s.ProcessPacket(p, new ObjectOutputStream(out) ,  null);
        assertEquals(s.getQueueSize(), 1);
        assertEquals("Adding new client to the queue!", outContent.toString().replaceAll("\r\n", ""));
        
        p = new Packet(OpCode.JoinQueue, 1, null);
        s.ProcessPacket(p, new ObjectOutputStream(out) ,  null);
        
    }
 

    @Test
    public void processPacketDefaultTest() throws IOException {
        p = new Packet(OpCode.UpdateBoard, 1, null);
        s.ProcessPacket(p, null,  null);
        String err = "Unknown packet opcode. Can only join queue from main server thread";
        assertEquals(err, errContent.toString().replaceAll("\r\n", ""));
    }

    
    @After
    public void tearDown() throws IOException {
        out.close();
        (new File("outputTest.dat")).delete();
    }
    
}
