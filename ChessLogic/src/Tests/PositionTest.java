package Tests;

import Game.Position;
import org.junit.*;

import static org.junit.Assert.*;

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

    @Test
    public void testConstructors()
    {
        Position p = new Position(1,5);
        Position p2 = new Position(p);

        assertEquals(p.GetX(), 1);
        assertEquals(p.GetY(), 5);

        assertTrue( p.GetX() == p2.GetX() && p.GetY() == p2.GetY() );
    }

    @Test
    public void positionEqualsTest()
    {
        Position p = new Position(2, 2);
        Position p2 = new Position(2, 2);
        Position p3 = new Position(2, 3);
        Position p35 = new Position(4, 2);
        Position p4 = new Position(4, 4);


        Assert.assertTrue(p.equals(p2));
        Assert.assertFalse(p.equals(p3));
        Assert.assertFalse(p.equals(p35));
        Assert.assertFalse(p.equals(p4));
    }

}
