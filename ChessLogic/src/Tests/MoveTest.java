package Tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import Game.Move;
import Game.Position;

public class MoveTest {

    Move m;
    Position start;
    Position end;
    @Before
    public void setUp() throws Exception {
        start = new Position(2,1) ;
        end = new Position(2,6);
        m = new Move(start, end);
    }

    @Test
    public void testGettersSetters() {
        assertEquals(m.GetStartX(),2);
        assertEquals(m.GetStartY(),1);
        
        assertEquals(m.GetEndX(),2);
        assertEquals(m.GetEndY(),6);
    }

}
