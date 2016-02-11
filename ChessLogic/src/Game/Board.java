package Game;

import Pieces.Piece;

/**
 * Created by Kyle on 2/1/2016.
 */
public class Board {

    static final int SIZE = 8;
    private BoardCell[][] m_boardState;

    /**
     * Default constructor initializes board to starting state for chess board
     * TODO: Set up board.
     */
    public Board(){
        m_boardState = new BoardCell[SIZE][SIZE];
        for(int i = 0; i < SIZE; i++)
        {
            for(int j = 0; j < SIZE; j++){
                m_boardState[i][j] = new BoardCell(i,j, null);
            }
        }

    }

    /**
     * Moves the Pieces by switching what board cell they belong to. Sets the startPos.piece
     * to null
     * @param move This cannot be null. The move to be applied to this board object
     */
    public void ApplyMove(Move move){
        Piece toMove = m_boardState[move.GetStartX()][move.GetStartY()].GetPiece();
        if(null != toMove) {
            m_boardState[move.GetEndX()][move.GetEndY()].SetPiece(toMove);
            m_boardState[move.GetStartX()][move.GetStartY()].SetPiece(null);
        }
    }

    /**
     * Overrides statndard toString from Object.
     * @return String representation of the board.
     */
    @Override
    public String toString(){
        String ret = "";
        for(int i = 0; i < m_boardState.length; i++){
            for (int j = 0; j < m_boardState[0].length; j++)
            {
                ret += m_boardState[i][j].toString();
            }
            ret+="\n";
        }
        return ret;
    }
}
