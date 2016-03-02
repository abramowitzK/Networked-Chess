package Tests;

import Game.*;
import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import Pieces.Color;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameTest {

    private PrintStream originalOut;
    @Before
    public void setUp()
    {
        JFXPanel panel = new JFXPanel();
        originalOut = System.out;
    }

    @Test
    public void quitTest() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        Player p1 = new Player(1, null, null, null);
        Player p2 = new Player(2, null, null, null);
        Game g = new Game(p1, p2);
        
        assertEquals(g.Quit(p1.GetID()),2);
        assertTrue(g.IsOver());
        System.setOut(originalOut);
    }
    
    @Test
    public void p2QuitTest() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        Player p1 = new Player(1, null, null, null);
        Player p2 = new Player(2, null, null, null);
        Game g = new Game(p1, p2);
        
        assertEquals(g.Quit(p2.GetID()), 1);
        assertTrue(g.IsOver());
        System.setOut(originalOut);
    }

    @Test
    public void getOtherPlayerTest()
    {

        Player p1 = new Player(1, null, null, null);
        Player p2 = new Player(2, null, null, null);
        Game g = new Game(p1, p2);
        assertEquals(g.getOtherPlayer(p1.GetID()), p2);
        assertEquals(g.getOtherPlayer(p2.GetID()), p1);

        assertEquals(g.getOtherPlayer(14), null);
    }

    @Test
    public void applyMoveGameTest()
    {
        Player p1 = new Player(1, null, null, null);
        Player p2 = new Player(2, null, null, null);
        Game g = new Game(p1, p2);

        Position start = new Position(1,0);
        Position end = new Position(3,0);
        Move m = new Move(start, end);
        g.ApplyMove(m);

        String expectedBoard = "Rook Black  \tKnight Black \tBishop Black \tQueen Black \tKing Black  \tBishop Black \tKnight Black \tRook Black  \t\n" +
                "EMPTY       \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \t\n" +
                "EMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "Pawn Black  \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "EMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "EMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "Pawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \t\n" +
                "Rook White  \tKnight White \tBishop White \tQueen White \tKing White  \tBishop White \tKnight White \tRook White  \t\n";
        assertEquals(g.getBoard().toString(), expectedBoard);

    }




    /*
        Implementation of the perft standardized chess test
     */
    public Integer perft(Board b, int depth) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int result = 0;
        int tempResult = 0;
        Color currColor = Color.White;
        if( depth % 2 == 0 )
        {
            currColor = Color.Black;
        }

        if( depth == 0 )
        {
            return 1;
        }
        for( int i=0; i < 8; i++ )
        {
            for(int j=0; j < 8; j++)
            {
                if( b.GetPiece(i,j) != null ) {
                    if (b.GetPiece(i, j).PieceColor == currColor) {
                        ArrayList<Position> moves = b.GetCheckedValidMoves(i, j);
                        for (Position p : moves) {
                            Move m = new Move(new Position(i, j), new Position(p.GetX(), p.GetY()));
                            Method method = b.getClass().getDeclaredMethod("CheckApplyMove", Move.class);
                            method.setAccessible(true);
                            method.invoke(b, m);

                            result += perft(b, depth - 1);

                            method = b.getClass().getDeclaredMethod("CheckUnApplyMove", Move.class);
                            method.setAccessible(true);
                            method.invoke(b, m);
                        }

                    }
                }
            }
        }
        return result;
    }

}
