package Tests;


import Game.*;
import Networking.OpCode;
import Networking.Packet;
import javafx.embed.swing.JFXPanel;
import org.junit.*;
import Server.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class ServerThreadTest {

    static ServerThread st;
    Server serv;
    Player p1, p2;
    ObjectOutputStream oos;
    Game g;
    private final static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    static PrintStream originalOut = System.out;

    @Before
    public void setUp() throws Exception {
        ByteArrayOutputStream newOut = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(newOut);
        ByteArrayOutputStream newOut2 = new ByteArrayOutputStream();
        ObjectOutputStream oos2 = new ObjectOutputStream(newOut);

        serv = new Server(5555, "127.0.0.1");
        Socket p2Socket = new Socket("127.0.0.1", 5555);
        Socket p1Socket = new Socket("127.0.0.1", 5555);
        p1 = new Player(1, null, oos, p1Socket);
        p2 = new Player(2, null, oos2, p2Socket);

        g = new Game(p1, p2);
        st = new ServerThread(p2, g, oos, null, serv);

        st.start();
    }


    public void processPacketUpdateBoardTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        System.setOut(originalOut);
        Move m = new Move(new Position(1,2), new Position(2,2));
        System.setOut(new PrintStream(outContent));
        ByteArrayOutputStream servOut = new ByteArrayOutputStream();
        ObjectOutputStream servOos = new ObjectOutputStream(servOut);
        Packet p = new Packet(OpCode.UpdateBoard, 1, m);
        Method method = st.getClass().getDeclaredMethod("ProcessPacket", Packet.class, ObjectOutputStream.class);
        method.setAccessible(true);
        method.invoke(st, p, servOos);

        Assert.assertEquals("received an update board packet from: 1\r\n", outContent.toString());
    }


    public void processPacketQuitTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException
    {


        ByteArrayOutputStream servOut = new ByteArrayOutputStream();
        ObjectOutputStream servOos = new ObjectOutputStream(servOut);
        Packet p = new Packet(OpCode.QuitGame, 1, null);
        Method method = st.getClass().getDeclaredMethod("ProcessPacket", Packet.class, ObjectOutputStream.class);
        method.setAccessible(true);
        method.invoke(st, p, servOos);
        Assert.assertTrue(g.getOtherPlayer(1).GetSocket().isClosed());

    }


    public void processPacketDefaultTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException
    {
        outContent.reset();
        System.setErr(new PrintStream(outContent));

        ByteArrayOutputStream servOut = new ByteArrayOutputStream();
        ObjectOutputStream servOos = new ObjectOutputStream(servOut);
        Packet p = new Packet(OpCode.JoinedQueue, 1, null);
        Method method = st.getClass().getDeclaredMethod("ProcessPacket", Packet.class, ObjectOutputStream.class);
        method.setAccessible(true);
        method.invoke(st, p, servOos);
        Assert.assertEquals("Unknown packet opcode\r\n", outContent.toString());
    }

    public void afterQuitApplyMove() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException
    {
        outContent.reset();
        System.setOut(new PrintStream(outContent));
        Move m = new Move(new Position(1,2), new Position(2,2));
        g.Quit(1);

        ByteArrayOutputStream servOut = new ByteArrayOutputStream();
        ObjectOutputStream servOos = new ObjectOutputStream(servOut);
        Packet p = new Packet(OpCode.UpdateBoard, 1, m);
        Method method = st.getClass().getDeclaredMethod("ProcessPacket", Packet.class, ObjectOutputStream.class);
        method.setAccessible(true);
        outContent.reset();
        method.invoke(st, p, servOos);

        String[] split = outContent.toString().split("\r\n");
        Assert.assertEquals("Game is over. Not forwarding packet", split[1]);
    }

    @Test
    public void processPacketTest()
    {
        try {
            processPacketUpdateBoardTest();
            processPacketDefaultTest();
            processPacketQuitTest();
            afterQuitApplyMove();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @AfterClass
    public static void tearDown()
    {
        st = null;
    }
}
