package Tests;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;


import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Test;

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
        s.ProcessPacket(p, new ObjectOutputStream(out) ,  null, null);
        assertEquals(s.getQueueSize(), 1);
        assertEquals("Adding new Client to the queue!", outContent.toString().replaceAll("\r\n", ""));
        
        p = new Packet(OpCode.JoinQueue, 1, null);
        s.ProcessPacket(p, new ObjectOutputStream(out) ,  null, null);
        
    }
 

    @Test
    public void processPacketDefaultTest() throws IOException {
        p = new Packet(OpCode.UpdateBoard, 1, null);
        s.ProcessPacket(p, null,  null, null);
        String err = "Unknown packet opcode. Can only join queue from main Server thread";
        assertEquals(err, errContent.toString().replaceAll("\r\n", ""));
    }

    
    @After
    public void tearDown() throws IOException {
        out.close();
        (new File("outputTest.dat")).delete();
    }
    
}
