package Tests;

import Game.Player;
import org.junit.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;

public class PlayerTest {

    
    static Player p1;
    static ServerSocket ss;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ss = new ServerSocket(2004);
        Socket s = new Socket("localhost", 2004);
        p1 = new Player(1, null, null, s);

    }
    
    
    @Test
    public void playerIdTest()
    {
        assertEquals(p1.GetID(), 1);
    }
    
    
    @Test
    public void inOutTest()
    {        
        assertNull(p1.GetIn());
        assertNull(p1.GetOut());
    }

    @Test
    public void testSocket()
    {
        assertEquals(p1.GetSocket().getPort(), 2004);
    }

    @After
    public void tearDown() throws IOException {
            ss.close();
    }

}
