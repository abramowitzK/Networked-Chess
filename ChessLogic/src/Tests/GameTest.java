package Tests;

import static org.junit.Assert.*;

import org.junit.Test;

import game.Game;
import game.Player;
import networking.OpCode;

public class GameTest {

    @Test
    public void quitTest() {
        Player p1 = new Player(1, null, null);
        Player p2 = new Player(2, null, null);
        Game g = new Game(p1, p2);
        
        assertEquals(g.Quit(p1.GetID()),2);
        assertTrue(g.IsOver());
    }
    
    @Test
    public void p2QuitTest() {
        Player p1 = new Player(1, null, null);
        Player p2 = new Player(2, null, null);
        Game g = new Game(p1, p2);
        
        assertEquals(g.Quit(p2.GetID()), 1);
        assertTrue(g.IsOver());
    }
    
   
}
