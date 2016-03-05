
package Tests;

import java.lang.System;
import Networking.*;
import Server.*;
import org.junit.*;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.*;

import static org.junit.Assert.*;

public class ServerTest {

    private final static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final static ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    static Server s;
    static Packet p;
    static FileOutputStream out;
    static PrintStream originalOut = System.out;

    @BeforeClass
    public static void setUp() throws Exception {
        s = new Server(4444, "127.0.0.1");
        p = new Packet(OpCode.JoinQueue, -1, null);

        out = new FileOutputStream("outputTest.dat");

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void failToBindPort()
    {
        exit.expectSystemExitWithStatus(-1);
        Server s2 = new Server(4444, "127.0.0.1");
    }

    @Test
    public void runTest() throws InterruptedException {
        System.setOut(new PrintStream(outContent));
        outContent.reset();
        Thread t = new Thread(new Runnable() {
            public void run() {
                s.Start();
            }
        });
        t.start();
        t.join(250);
        outContent.reset();
    }

    @Test
    public void processPacketJoinQuitTest() throws IOException
    {
        out = new FileOutputStream("outputTest.dat");
        p = new Packet(OpCode.JoinQueue, 1, null);
        s.ProcessPacket(p, new ObjectOutputStream(outContent), null, null);
        ByteArrayOutputStream newOut = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(newOut);
        oos.writeObject(new Packet(OpCode.JoinedQueue, 1, null));
        assertEquals(newOut.toString(), outContent.toString());

        outContent.reset();
        System.setOut(new PrintStream(outContent));
        p = new Packet(OpCode.JoinQueue, -1, null);
        s.ProcessPacket(p, new ObjectOutputStream(out), null, null);
        assertEquals(1, s.getQueueSize());

    }

    @Test
    public void processPacketDefaultTest() throws IOException {
        errContent.reset();
        p = new Packet(OpCode.UpdateBoard, 1, null);
        s.ProcessPacket(p, null,  null, null);
    }

    
    @AfterClass
    public static void tearDown() throws IOException {
        out.close();
        System.setOut(originalOut);
        System.setErr(System.err);
        (new File("outputTest.dat")).delete();
        s = null;
    }
    
}

