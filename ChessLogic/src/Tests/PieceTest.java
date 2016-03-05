package Tests;

import org.junit.*;
import Pieces.*;
import Game.*;

public class PieceTest {

    @Test
    public void hasMovedTest()
    {
        Board b = new Board();
        Piece p = b.GetPiece(1,0);
        assert(!p.HasMoved());

        Position start = new Position(1,0);
        Position end = new Position(3,0);
        Move m = new Move(start, end);
        b.ApplyMove(m);
        assert(p.HasMoved());

        p.UnsetHasMoved();
        assert(!p.HasMoved());
    }

    @Test
    public void emptyPieceTest()
    {
        System.setOut(System.out);
        Piece p = new Piece(PieceType.Empty, Color.Empty);
        Piece p2 = new Piece(PieceType.Empty, Color.Empty);
        Assert.assertEquals("Empty Empty ", p.toString());
        Assert.assertEquals("Empty Empty ", p2.toString());
    }
}
