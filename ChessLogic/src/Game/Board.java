package Game;

import Pieces.Color;
import Pieces.Piece;
import Pieces.PieceType;

import java.util.ArrayList;

public class Board {
    private boolean m_blackCheck;
    private boolean m_whiteCheck;
    static final int SIZE = 8;
    private Piece[][] m_boardState;
    /**
     * Default constructor initializes board to starting state for chess board
     * TODO: Set up board.
     */
    public Board(){
        m_boardState = new Piece[SIZE][SIZE];
        m_boardState[0][0] = new Piece(PieceType.Rook, Color.Black);
        m_boardState[0][1] = new Piece(PieceType.Knight, Color.Black);
        m_boardState[0][2] = new Piece(PieceType.Bishop, Color.Black);
        m_boardState[0][3] = new Piece(PieceType.Queen, Color.Black);
        m_boardState[0][4] = new Piece(PieceType.King, Color.Black);
        m_boardState[0][5] = new Piece(PieceType.Bishop, Color.Black);
        m_boardState[0][6] = new Piece(PieceType.Knight, Color.Black);
        m_boardState[0][7] = new Piece(PieceType.Rook, Color.Black);

        for(int i = 0; i < SIZE; i++){
            m_boardState[1][i] = new Piece(PieceType.Pawn, Color.Black);
        }
        for(int i = 2; i < 6; i++){
            for(int j = 0; j < SIZE; j++){
                //m_boardState[i][j] = new Piece(PieceType.Empty, Color.Empty);
                m_boardState[i][j] = null;
            }
        }
        for(int i = 0; i < SIZE; i++){
            m_boardState[6][i] = new Piece(PieceType.Pawn, Color.White);
        }
        m_boardState[7][0] = new Piece(PieceType.Rook, Color.White);
        m_boardState[7][1] = new Piece(PieceType.Knight, Color.White);
        m_boardState[7][2] = new Piece(PieceType.Bishop, Color.White);
        m_boardState[7][3] = new Piece(PieceType.Queen, Color.White);
        m_boardState[7][4] = new Piece(PieceType.King, Color.White);
        m_boardState[7][5] = new Piece(PieceType.Bishop, Color.White);
        m_boardState[7][6] = new Piece(PieceType.Knight, Color.White);
        m_boardState[7][7] = new Piece(PieceType.Rook, Color.White);

    }
    /**
     * Moves the Pieces by switching what board cell they belong to. Sets the startPos.piece
     * to null
     * @param move This cannot be null. The move to be applied to this board object
     */
    public void ApplyMove(Move move){
        Position start = new Position(move.GetStartX(), move.GetStartY());
        Position end = new Position(move.GetEndX(), move.GetEndY());
        Piece temp = m_boardState[start.GetX()][start.GetY()];
        temp.SetHasMoved();
        SetPiece(start.GetX(),start.GetY(), null);
        SetPiece(end.GetX(), end.GetY(), temp);

    }
    public Piece GetPiece(int i , int j){
        return m_boardState[i][j];
    }
    public void SetPiece(int i, int j, Piece p){
        if(p != null)
            p.SetHasMoved();
        m_boardState[i][j] = p;
    }
    public Position GetPos(Piece p){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(p == m_boardState[i][j])
                    return new Position(i,j);
            }
        }
        return null;
    }
    public ArrayList<Position> GetValidMoves(int i, int j){

        Piece p = m_boardState[i][j];
        ArrayList<Position> ret = null;
        int dir;
        if(p.PieceColor == Color.Black)
            dir = 1;
        else
            dir = -1;
        switch (p.Type){
            case Pawn:
                ret = GetValidPawnMoves(i,j, dir, p);
                break;
        }
        return ret;
    }
    private boolean WithinBounds(int i){
        if(i < 8 && i > 0)
            return true;
        else
            return false;
    }
    private ArrayList<Position> GetValidPawnMoves(int i, int j, int dir, Piece p){
        ArrayList<Position> ret = new ArrayList<>();
            //First check directly in front of us
        if(i+dir < 8 && m_boardState[i+dir][(j)] == null) {
            ret.add(new Position(i+dir, j));
            if (!p.HasMoved() && m_boardState[i+2*dir][j] == null)
                ret.add(new Position(i+2*dir, j));
        }
        if(WithinBounds(i+dir) && WithinBounds(j+dir) && m_boardState[i+dir][j+dir]!= null && m_boardState[i+dir][j+dir].PieceColor != p.PieceColor)
            ret.add(new Position(i+dir, j+dir));
        if(WithinBounds(i+dir) && WithinBounds(j-dir) && m_boardState[i+dir][j-dir]!= null && m_boardState[i+dir][j-dir].PieceColor != p.PieceColor)
            ret.add(new Position(i+dir, j-dir));
        return  ret;
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
