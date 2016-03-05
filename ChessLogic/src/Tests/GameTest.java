package Tests;

import Game.*;
import Pieces.Piece;
import Pieces.PieceType;
import javafx.embed.swing.JFXPanel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import Tests.BoardTest;
import java.io.ByteArrayOutputStream;
import Pieces.Color;
import java.io.PrintStream;


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
    public void setBoardTest()
    {
        Board b = new BoardTest().clearBoard();
        Player p1 = new Player(1, null, null, null);
        Player p2 = new Player(2, null, null, null);
        Game g = new Game(p1, p2);
        g.setBoard(b);

        Assert.assertEquals(b, g.getBoard());
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


    @Test
    public void gameCastleTest()
    {
        Player p1 = new Player(1, null, null, null);
        Player p2 = new Player(2, null, null, null);
        Game g = new Game(p1, p2);
        g.Castle(Color.Black, true);
    }



   
}
