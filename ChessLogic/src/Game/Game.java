package Game;

/**
 * Created by Kyle on 2/1/2016.
 */
public class Game {
    private Player m_player1;
    private int m_p1ID;
    private Player m_player2;
    private int m_p2ID;
    private Board m_board;
    public Game(Player player1, Player player2){
        m_player1 = player1;
        m_p1ID = player1.GetID();
        m_player2 = player2;
        m_p2ID = player2.GetID();
        m_board = new Board();
    }
    public void ApplyMove(int playerID, Move move){
        m_board.ApplyMove(move);
        if(playerID == m_p1ID){
            //Send to player2
        }
        else{
            //Send to Player1
        }
    }
    public void Quit(int playerID){

    }
}
