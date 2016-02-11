package Tests;

import static org.junit.Assert.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.BeforeClass;
import org.junit.Test;

import game.Player;

public class PlayerTest {

    
    static Player p1;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        p1 = new Player(1, null, null);
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
