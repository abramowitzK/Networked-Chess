package Tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.sun.istack.internal.Nullable;

import Game.BoardCell;
import Pieces.Piece;

public class BoardCellTest {

    BoardCell bc;
    Piece p;
    Piece newPiece;
    @Before
    public void setUp() throws Exception {
        p = new Piece("Pawn");
        newPiece  = new Piece("Queen");
        bc = new BoardCell(1,4, p);   
    }

    @Test
    public void testGetPiece(){
        assertEquals(bc.GetPiece(), p);
    }
    
    @Test
    public void testSetPiece(){
        bc.SetPiece(newPiece);
        assertEquals(bc.GetPiece(), newPiece);
    }
    
    @Test
    public void testToString(){
        bc.SetPiece(p);
        assertEquals(bc.toString(), "Pawn");
        
        bc.SetPiece(newPiece);
        assertEquals(bc.toString(), "Queen");

        bc.SetPiece(null);
        assertEquals(bc.toString(), "N");
        
    }

}
