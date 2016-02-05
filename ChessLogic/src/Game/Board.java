package Game;

/**
 * Created by Kyle on 2/1/2016.
 */
public class Board {

    private BoardCell[][] m_boardState;

    /**
     * Default constructor initializes board to starting state for chess board
     */
    public Board(){
        m_boardState = new BoardCell[8][8];
        //Do initialization here.
    }
    public void ApplyMove(Move move){
        m_boardState[move.GetEndX()][move.GetEndY()].SetPiece(m_boardState[move.GetStartX()][move.GetStartY()].GetPiece());
        m_boardState[move.GetStartX()][move.GetStartY()].SetPiece(null);
    }
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
