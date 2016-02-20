package Tests;

import Game.Player;
import org.junit.*;

import static org.junit.Assert.*;

public class PlayerTest {

    
    static Player p1;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        p1 = new Player(1, null, null, null);
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

}
