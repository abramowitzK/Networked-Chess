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

    }
}
