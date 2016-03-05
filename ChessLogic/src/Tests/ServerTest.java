
package Tests;

import java.lang.System;
import Networking.*;
import Server.*;
import org.junit.*;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.TestRule;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

        Packet quitPacket = new Packet(OpCode.QuitGame, 0, null);
        s.ProcessPacket(quitPacket, null, null, null);
        assertEquals(1, s.getQueueSize());
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

