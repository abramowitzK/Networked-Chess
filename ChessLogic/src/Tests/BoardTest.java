package Tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import Game.Board;

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
