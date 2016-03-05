package Tests;

import Game.*;
import Pieces.*;
import javafx.embed.swing.JFXPanel;
import org.junit.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {


    @Before
    public void setUp() throws Exception {
        JFXPanel panel = new JFXPanel();
    }

    @Test
    public void applyMoveTest()
    {
        Board b = new Board();
        Position start = new Position(1,0);
        Position end = new Position(3,0);
        Move m = new Move(start, end);
        b.ApplyMove(m);

        String expectedBoard = "Rook Black  \tKnight Black \tBishop Black \tQueen Black \tKing Black  \tBishop Black \tKnight Black \tRook Black  \t\n" +
                "EMPTY       \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \t\n" +
                "EMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "Pawn Black  \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "EMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "EMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "Pawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \t\n" +
                "Rook White  \tKnight White \tBishop White \tQueen White \tKing White  \tBishop White \tKnight White \tRook White  \t\n";
        assertEquals(b.toString(), expectedBoard);
    }


    @Test
    public void boardInitialStateTest() {
        Board b = new Board();
        String initialBoard = "Rook Black  \tKnight Black \tBishop Black \tQueen Black \tKing Black  \tBishop Black \tKnight Black \tRook Black  \t\n" +
                "Pawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \tPawn Black  \t\n" +
                "EMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "EMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "EMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "EMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \tEMPTY       \t\n" +
                "Pawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \tPawn White  \t\n" +
                "Rook White  \tKnight White \tBishop White \tQueen White \tKing White  \tBishop White \tKnight White \tRook White  \t\n";
        assertEquals(initialBoard, b.toString());
    }


    @Test
    public void GetCheckedValidMovesBackRowTest()
    {
        Board b = new Board();
        int expected;
        ArrayList<Position> validMoves;
        //Check all pieces in topmost and bottommost row which should have no moves
        for( int i=0; i < 8; i ++ )
        {
            if( i != 1 && i != 6)
            {
                expected = 0;
            }
            else
            {
                expected = 2;
            }
            validMoves = (ArrayList<Position>) b.GetCheckedValidMoves(0,i);
            assertEquals(validMoves.size(), expected);

            validMoves = (ArrayList<Position>) b.GetCheckedValidMoves(7,i);
            assertEquals(validMoves.size(), expected);
        }
    }

    @Test
    public void unmovedPawnMovementTestBlack()
    {
        // Pawn hasn't moved so it has two possible moves
        Board b = new Board();
        for( int i=0; i < 8; i++)
        {
            int x = 1;
            int y = i;
            Position p = new Position(x+1,y);
            Position p2 = new Position(x+2,y);

            // Confirm that Pawn has two moves when it hasn't moved
            ArrayList<Position> expectedMoves = new ArrayList<>();
            expectedMoves.add(p);   expectedMoves.add(p2);
            ArrayList<Position> validMoves = (ArrayList<Position>)b.GetCheckedValidMoves(x, y);
            assertEquals(validMoves.toString(), expectedMoves.toString());
        }
    }

    @Test
    public void unmovedPawnMovementTestWhite()
    {
        Board b = new Board();

        for( int i=0; i < 8; i++)
        {
            int x = 6;
            int y = i;
            Position p = new Position(x-1,y);
            Position p2 = new Position(x-2,y);

            // Confirm that Pawn has two moves when it hasn't moved
            ArrayList<Position> expectedMoves = new ArrayList<>();
            expectedMoves.add(p);   expectedMoves.add(p2);
            ArrayList<Position> validMoves = (ArrayList<Position>)b.GetCheckedValidMoves(x, y);
            assertEquals(validMoves.toString(), expectedMoves.toString());
        }
    }


    @Test
    public void movedPawnMovementTest()
    {
        Board b = new Board();

        for(int i=0; i < 8; i++)
        {
            Position start;// = new Position(1,0);
            Position end; //= new Position(3,0);

            int xWhite = 6;
            int y = i;

            Position pWhite = new Position(xWhite-2,y);
            b.ApplyMove(new Move(new Position(xWhite,y), new Position(xWhite-1,y)));


            int xBlack = 1;
            Position pBlack = new Position(xBlack+2, y);
            b.ApplyMove(new Move(new Position(xBlack,y), new Position(xBlack+1,y)));

            ArrayList<Position> expectedMoves = new ArrayList<>();
            expectedMoves.add(pWhite);
            ArrayList<Position> validMoves = (ArrayList<Position>)b.GetCheckedValidMoves(xWhite-1, y);
            assertEquals(validMoves.toString(), expectedMoves.toString());

            expectedMoves.clear();
            validMoves.clear();
            expectedMoves.add(pBlack);
            validMoves = (ArrayList<Position>)b.GetCheckedValidMoves(xBlack+1, y);
            assertEquals(validMoves.toString(), expectedMoves.toString());
        }
    }


    @Test
    public void emptyBoardCellMoveTest()
    {
        System.setOut(System.out);
        Board b = new Board();
        assertEquals(0, b.GetCheckedValidMoves(4, 4).size());

    }

    /*
        Corresponding pawns in each column are moved forward 2 spaces blocking those pawns from moving.
        Checks each pawn to confirm that each pawn has no possible moves.
    */
    @Test
    public void pawnPieceInFrontTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Board b = new Board();
        Method method;

        int xWhite = 6;
        int xBlack = 1;
        for(int y=0; y < 8; y++) {
            Move whiteMove = new Move(new Position(xWhite, y), new Position(xWhite - 2, y));
            Move blackMove = new Move(new Position(xBlack, y), new Position(xBlack + 2, y));


            method = b.getClass().getDeclaredMethod("CheckApplyMove", Move.class);
            method.setAccessible(true);
            method.invoke(b, whiteMove);
            method.invoke(b, blackMove);

            assertEquals(b.GetCheckedValidMoves(xWhite - 2, y).size(), 0);
            assertEquals(b.GetCheckedValidMoves(xBlack + 2, y).size(), 0);

            method = b.getClass().getDeclaredMethod("CheckUnApplyMove", Move.class);
            method.setAccessible(true);
            method.invoke(b, whiteMove);
            method.invoke(b, blackMove);
        }
    }


    /*
        Tests to make sure that CheckApplyMove() and CheckUnApplyMove() function properly.
     */
    @Test
    public void tempPieceMovementTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Board b = new Board();

        String initialBoard = b.toString();
        Position start = new Position(6, 0);
        Position end = new Position(5, 0);
        Move m = new Move(start, end);

        Method method = b.getClass().getDeclaredMethod("CheckApplyMove", Move.class);
        method.setAccessible(true);
        method.invoke(b, m);

        method = b.getClass().getDeclaredMethod("CheckUnApplyMove", Move.class);
        method.setAccessible(true);
        method.invoke(b, m);

        assertEquals(initialBoard, b.toString());
    }

    public Board clearBoard()
    {
        Board b = new Board();
        Piece p;
        for( int i=0; i < 8; i++)
        {
            for( int j=0; j < 8; j++)
            {
                p = b.GetPiece(i,j);
                if( p != null )
                {
                    if( p.Type != PieceType.King )
                    {
                        b.SetPiece(i,j, null);
                    }
                }
            }
        }
        return b;
    }

    private boolean compareMoves(String[] expected, ArrayList<Position> positions)
    {
        String[] splitString;
        String s, curr;
        Position pos;
        for( int i=0; i < expected.length; i++ )
        {
            curr = expected[i];
            s = curr.replaceAll("\\s+","").substring(1, curr.length()-2);
            splitString = s.split(",");
            pos = new Position(Integer.parseInt( splitString[0] ), Integer.parseInt( splitString[1] ));

            if( !positions.contains(pos) )
            {
                return false;
            }
        }
        return true;
    }

    @Test
    public void bishopMovementTest() {
        Board b = clearBoard();

        b.SetPiece(7, 2, new Piece(PieceType.Bishop, Color.White));
        b.SetPiece(7, 5, new Piece(PieceType.Bishop, Color.White));

        b.SetPiece(0, 2, new Piece(PieceType.Bishop, Color.Black));
        b.SetPiece(0, 5, new Piece(PieceType.Bishop, Color.Black));


        ArrayList<Position> whiteLeft = (ArrayList<Position>)b.GetCheckedValidMoves(7, 2);
        ArrayList<Position> whiteRight = (ArrayList<Position>)b.GetCheckedValidMoves(7, 5);
        ArrayList<Position> blackLeft = (ArrayList<Position>)b.GetCheckedValidMoves(0, 2);
        ArrayList<Position> blackRight = (ArrayList<Position>)b.GetCheckedValidMoves(0, 5);

        String[] expectedWhiteLeft = {"(2, 7)", "(6, 1)", "(6, 3)", "(3, 6)", "(4, 5)", "(5, 4)", "(5, 0)"};
        String[] expectedWhiteRight = {"(6, 4)", "(6, 6)", "(4, 2)", "(5, 3)", "(5, 7)", "(2, 0)", "(3, 1)"};
        String[] expectedBlackLeft = {"(1, 3)", "(2, 0)", "(2, 4)", "(5, 7)", "(3, 5)", "(4, 6)", "(1, 1)"};
        String[] expectedBlackRight = {"(2, 3)", "(2, 7)", "(4, 1)", "(3, 2)", "(1, 4)", "(1, 6)", "(5, 0)"};

        assert(compareMoves(expectedWhiteLeft, whiteLeft));
        assert(compareMoves(expectedWhiteRight, whiteRight));
        assert(compareMoves(expectedBlackLeft, blackLeft));
        assert(compareMoves(expectedBlackRight, blackRight));
    }

    @Test
    public void knightMovementTest()
    {
        Board b = clearBoard();

        b.SetPiece(7, 1, new Piece(PieceType.Knight, Color.White));
        b.SetPiece(7, 6, new Piece(PieceType.Knight, Color.White));
        b.SetPiece(0, 1, new Piece(PieceType.Knight, Color.Black));
        b.SetPiece(0, 6, new Piece(PieceType.Knight, Color.Black));

        ArrayList<Position> whiteLeft = (ArrayList<Position>)b.GetCheckedValidMoves(7, 1);
        ArrayList<Position> whiteRight = (ArrayList<Position>)b.GetCheckedValidMoves(7, 6);
        ArrayList<Position> blackLeft = (ArrayList<Position>)b.GetCheckedValidMoves(0, 1);
        ArrayList<Position> blackRight = (ArrayList<Position>)b.GetCheckedValidMoves(0, 6);

        String[] expectedWhiteLeft = {"(5, 0)", "(6, 3)", "(5, 2)"};
        String[] expectedWhiteRight = {"(6, 4)", "(5, 7)", "(5, 5)"};
        String[] expectedBlackLeft = {"(1, 3)", "(2, 0)", "(2, 2)"};
        String[] expectedBlackRight = {"(2, 7)", "(2, 5)", "(1, 4)"};


        Assert.assertTrue(compareMoves(expectedWhiteLeft, whiteLeft));
        Assert.assertTrue(compareMoves(expectedWhiteRight, whiteRight));
        Assert.assertTrue(compareMoves(expectedBlackLeft, blackLeft));
        Assert.assertTrue(compareMoves(expectedBlackRight, blackRight));
    }

    @Test
    public void rookMovementTest()
    {
        Board b = clearBoard();

        //Put Rooks on the Board
        b.SetPiece(7, 0, new Piece(PieceType.Rook, Color.White));
        b.SetPiece(7, 7, new Piece(PieceType.Rook, Color.White));
        b.SetPiece(0, 0, new Piece(PieceType.Rook, Color.Black));
        b.SetPiece(0, 7, new Piece(PieceType.Rook, Color.Black));


        //Get valid rook moves
        ArrayList<Position> whiteLeft = (ArrayList<Position>)b.GetCheckedValidMoves(7, 0);
        ArrayList<Position> whiteRight = (ArrayList<Position>)b.GetCheckedValidMoves(7, 7);
        ArrayList<Position> blackLeft = (ArrayList<Position>)b.GetCheckedValidMoves(0, 0);
        ArrayList<Position> blackRight = (ArrayList<Position>)b.GetCheckedValidMoves(0, 7);


        //Set expected moves for the 4 rooks on the board
        String[] expectedWhiteLeft = {"(3, 0)", "(6, 0)", "(5, 0)", "(7, 3)", "(7, 1)", "(1, 0)", "(0, 0)", "(4, 0)", "(7, 2)", "(2, 0)"};
        String[] expectedWhiteRight = {"(2, 7)", "(0, 7)", "(1, 7)", "(7, 6)", "(5, 7)", "(7, 5)", "(6, 7)", "(3, 7)", "(4, 7)"};
        String[] expectedBlackLeft = {"(2, 0)", "(1, 0)", "(6, 0)", "(0, 1)", "(3, 0)", "(0, 2)", "(4, 0)", "(5, 0)", "(7, 0)", "(0, 3)"};
        String[] expectedBlackRight = {"(6, 7)", "(2, 7)", "(7, 7)", "(3, 7)", "(1, 7)", "(0, 6)", "(5, 7)", "(0, 5)", "(4, 7)"};


        // Check that the generated moves are the ones we expected
        Assert.assertTrue(compareMoves(expectedWhiteLeft, whiteLeft));
        Assert.assertTrue(compareMoves(expectedWhiteRight, whiteRight));
        Assert.assertTrue(compareMoves(expectedBlackLeft, blackLeft));
        Assert.assertTrue(compareMoves(expectedBlackRight, blackRight));

    }


    @Test
    public void queenMovementTest()
    {
        Board b = clearBoard();
        b.SetPiece(3, 3, new Piece(PieceType.Queen, Color.White));
        ArrayList<Position> whiteQueen = (ArrayList<Position>)b.GetCheckedValidMoves(3,3);
        b.SetPiece(3, 3, new Piece(PieceType.Queen, Color.Black));
        ArrayList<Position> blackQueen = (ArrayList<Position>)b.GetCheckedValidMoves(3,3);
        String[] expected = {"(0, 0)", "(1, 3)", "(3, 7)", "(5, 1)", "(5, 5)", "(3, 6)", "(3, 0)", "(3, 5)", "(3, 2)", "(0, 3)", "(6, 0)", "(4, 4)", "(2, 4)", "(4, 3)", "(0, 6)", "(4, 2)",
                "(1, 5)", "(1, 1)", "(3, 4)", "(7, 3)", "(6, 3)", "(3, 1)", "(2, 3)", "(2, 2)", "(7, 7)", "(6, 6)", "(5, 3)"};
        assertEquals(compareMoves(expected, whiteQueen), true);
        assertEquals(compareMoves(expected, blackQueen), true);
    }


    @Test
    public void kingMovementTest()
    {
        Board b = clearBoard();
        ArrayList<Position> whiteKing = (ArrayList<Position>)b.GetCheckedValidMoves(7, 4);
        ArrayList<Position> blackKing = (ArrayList<Position>)b.GetCheckedValidMoves(0, 4);

        String[] expectedWhite = {"(6, 3)", "(7, 3)", "(6, 5)", "(6, 4)", "(7, 5)"};
        String[] expectedBlack = {"(0, 3)", "(1, 5)", "(1, 4)", "(0, 5)", "(1, 3)"};

        Assert.assertTrue(compareMoves(expectedWhite, whiteKing));
        Assert.assertTrue(compareMoves(expectedBlack, blackKing));
    }


    public Board tempApplyMove(Board b, Move m)
    {

        try {
            Method method = null;
            method = b.getClass().getDeclaredMethod("CheckApplyMove", Move.class);
            method.setAccessible(true);
            method.invoke(b, m);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return b;
    }

    public Board tempUnApplyMove(Board b, Move m)
    {
        try {
            Method method = null;
            method = b.getClass().getDeclaredMethod("CheckUnApplyMove", Move.class);
            method.setAccessible(true);
            method.invoke(b, m);
        } catch (IllegalAccessException e) {
        e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return b;
    }


    @Test
    public void blackCheckTest(){
        Board b = clearBoard();
        b.SetPiece(4,4, new Piece(PieceType.Rook, Color.White));
        assertTrue(b.GetCheck(Color.Black));
        ArrayList<Position> positions = (ArrayList<Position>)b.GetCheckedValidMoves(0, 4);
        Position start = new Position(0, 4);
        Position end;

        for(Position pos : positions )
        {
            end = pos;
            Move m = new Move(start, end);

            b = tempApplyMove(b, m);
            assertFalse(b.IsInCheck(Color.Black));
            b = tempUnApplyMove(b,m);
        }
    }

    @Test
    public void whiteCheckTest()
    {
        Board b = clearBoard();
        b.SetPiece(3,4, new Piece(PieceType.Rook, Color.Black));
        assertTrue(b.GetCheck(Color.White));
        ArrayList<Position> positions =(ArrayList<Position>) b.GetCheckedValidMoves(7, 4);
        Position start = new Position(7, 4);
        Position end;

        for(Position pos : positions )
        {
            end = pos;
            Move m = new Move(start, end);

            b = tempApplyMove(b, m);
            assertFalse(b.IsInCheck(Color.White));
            b = tempUnApplyMove(b,m);
        }
    }

    @Test
    public void whiteCheckmateTest()
    {
        Board b = new Board();
        b.ApplyMove(new Move(new Position(1,4), b.GetCheckedValidMoves(1, 4).get(1)));
        b.ApplyMove(new Move(new Position(6,5), b.GetCheckedValidMoves(6, 5).get(0)));
        b.ApplyMove(new Move(new Position(6,6), b.GetCheckedValidMoves(6, 6).get(1)));
        b.ApplyMove(new Move(new Position(0,3), new Position(4,7)));
        Assert.assertTrue(b.IsInCheckmate(Color.White));

        b.ApplyMove(new Move(new Position(0,5), new Position(4,7)));
        Assert.assertTrue(b.IsInCheckmate(Color.White));

        b.ApplyMove(new Move(new Position(1,5), new Position(6,5)));
        Assert.assertTrue(b.IsInCheckmate(Color.White));

        b = clearBoard();
        tempApplyMove(b, new Move(new Position(0,4), new Position(2,0)));
        tempApplyMove(b, new Move(new Position(7,4), new Position(0,0)));
        b.SetPiece(7,2, new Piece(PieceType.Rook, Color.Black));

        assertFalse(b.IsInCheckmate(Color.White));
        b.ApplyMove(new Move(new Position(7,2), new Position(0,2)));
        Assert.assertTrue(b.IsInCheckmate(Color.White));
    }


    @Test
    public void blackCheckmateTest()
    {
        Board b = new Board();
        b.ApplyMove(new Move(new Position(6,4), b.GetCheckedValidMoves(6, 4).get(1)));
        b.ApplyMove(new Move(new Position(1,5), b.GetCheckedValidMoves(1, 5).get(0)));
        b.ApplyMove(new Move(new Position(1,6), b.GetCheckedValidMoves(1, 6).get(1)));
        b.ApplyMove(new Move(new Position(7,3), new Position(3,7)));
        Assert.assertTrue(b.IsInCheckmate(Color.Black));

        b.ApplyMove(new Move(new Position(7,5), new Position(3,7)));
        Assert.assertTrue(b.IsInCheckmate(Color.Black));

        b = clearBoard();
        tempApplyMove(b, new Move(new Position(0,4), new Position(0,0)));
        tempApplyMove(b, new Move(new Position(7,4), new Position(2,0)));
        b.SetPiece(7,2, new Piece(PieceType.Rook, Color.White));

        assertFalse(b.IsInCheckmate(Color.Black));
        b.ApplyMove(new Move(new Position(7,2), new Position(0,2)));
        Assert.assertTrue(b.IsInCheckmate(Color.Black));

    }
    

}

