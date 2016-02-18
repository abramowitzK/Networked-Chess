package Pieces;

import Game.Board;
import Game.BoardCell;
import Game.Position;

import java.util.ArrayList;
public class Pawn extends Piece {
    private boolean m_hasMoved;
    private boolean m_upForPromotion;
    public Pawn(String name, BoardCell cell, boolean isBlack){
        super(name, cell, isBlack);
    }

    /**
     * TODO Validation
     * Generates a list of moves for purposes of highlighting possible moves. Will handle validation
     * @param board board used to determine if move is possible due to check/pieces blocking
     * @return list of valid moves
     */
    @Override
    public ArrayList<Position> GenerateMoves(Board board) {
        int dir;
        if(IsBlack())
            dir = -1;
        else
            dir = 1;
        Position current = GetPosition();
        ArrayList<Position> ret = new ArrayList<>();
        //Generate basic moves
        //If there is no piece directly in front of us we can move
        if(null == board.GetBoardCell(current.GetX(), current.GetY() + dir).GetPiece()) {
            ret.add(new Position(current.GetX(), current.GetY() + dir));
            if (!m_hasMoved) {
                if (null == board.GetBoardCell(current.GetX(), current.GetY() + dir * 2))
                    ret.add(new Position(current.GetX(), current.GetY() + 2 * dir));
            } else {
                if ((current.GetY() + dir) > 7 || (current.GetY() + dir) < 1) {
                    if (null == board.GetBoardCell(current.GetX(), current.GetY() + dir).GetPiece())
                        m_upForPromotion = true;
                }
            }
        }
        return ret;
    }
    public boolean IsUpForPromotion(){
        return m_upForPromotion;
    }
    public void SetHasMoved(){
        m_hasMoved = true;
    }
}
