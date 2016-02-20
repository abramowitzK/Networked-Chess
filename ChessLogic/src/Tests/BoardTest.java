package Tests;

import Game.Board;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {

    Board b;
    @Before
    public void setUp() throws Exception {
        b = new Board();
    }

    @Test
    public void toStringTest() {
        System.out.print(b.toString());
    }

}
