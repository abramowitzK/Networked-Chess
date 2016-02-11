package game;

import networking.*;

import java.io.IOException;
import java.net.SocketException;

/**
 * Created by Kyle on 2/1/2016.
 */
public class Game {
    private Player m_player1;
    private int m_p1ID;
    private Player m_player2;
    private int m_p2ID;
    private Board m_board;
    private boolean m_isOver;

    /**
     * Order does not matter when specifying players. These should be two players in the same game logically
     * @param player1 First player
     * @param player2 Second player
     */
    public Game(Player player1, Player player2){
        m_player1 = player1;
        m_p1ID = player1.GetID();
        m_player2 = player2;
        m_p2ID = player2.GetID();
        m_board = new Board();
    }

    /**
     * This function applies the move to the server board which is located in the game object that
     * is shared between the two ServerThreads. The game object is sycnhronized so concurrent access
     * shouldn't be a problem. It then determines which player sent the update and sends an update to
     * the other client accordingly.
     * @param move Cannot be null. Move to be applied to the game.
     */
    public void ApplyMove(Move move){
        m_board.ApplyMove(move);
    }
    /**
     * Function used to quit them game. Sets a boolean flag that is used to determine when game is over.
     * @param playerID Player who quit the game. Other player automatically wins.
     * @return Player who wins the game.
     */
    public int Quit(int playerID){
        m_isOver = true;
        if (playerID == m_p1ID) {
            System.out.println("Player 1 quit");
            return m_p2ID;
        }
        else {
            System.out.println("Player 2 quit");
            return m_p1ID;
        }

    }

    /**
     * Call to determine status of game (over/not over)
     * @return If game is over or not
     */
    public boolean IsOver(){
        return m_isOver;
    }

    /**
     * Given a player ID returns the other player ID
     * @param playerID
     * @return
     */
    public Player getOtherPlayer(int playerID){
        if(playerID == m_p1ID)
            return m_player2;
        else if (playerID == m_p2ID)
            return m_player1;
        else
            return null;
    }
}
