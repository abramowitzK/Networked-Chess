package Tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Game.Position;

public class PositionTest {

    static Position p;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        p = new Position(5,5);
    }

    @Test
    public void testGetX() {
        assertEquals(p.GetX(), 5);
    }
    
    @Test
    public void testGetY() {
        assertEquals(p.GetY(), 5);
    }
    
    @Test
    public void testSetX() {
        p.SetX(1);
        assertEquals(p.GetX(), 1);
    }
    
    @Test
    public void testSetY() {
        p.SetY(1);
        assertEquals(p.GetX(), 1);
    }
}
