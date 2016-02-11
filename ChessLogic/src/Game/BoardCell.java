package Game;

import Pieces.Piece;


/**
 * Created by Kyle_Windows10 on 2/4/2016.
 */
public class BoardCell {
    /**
     * Reference to a piece object. Can be null
     */
    private Piece m_piece;
    private Position m_pos;
    public BoardCell(int x, int y, Piece piece){
        m_pos = new Position(x, y);
        m_piece = piece;
    }
    public Piece GetPiece(){
        return m_piece;
    }
    public void SetPiece(Piece piece){
        m_piece = piece;
    }
    @Override
    public String toString(){
        if(null != m_piece){
            return m_piece.GetName();
        } else{
            return "No Name";
        }
    }
}
