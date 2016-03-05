package Game;

import Pieces.Color;

public class Game {
    private final Player m_player1;
    private int m_p1ID;
    private Player m_player2;
    private int m_p2ID;
    private Board m_board;
    private boolean m_isOver;
    /**
     * Order does not matter when specifying players. These should be two players in the same Game logically
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
     * This function applies the move to the Server board which is located in the Game object that
     * is shared between the two ServerThreads. The Game object is synchronized so concurrent access
     * shouldn't be a problem. It then determines which player sent the update and sends an update to
     * the other Client accordingly.
     * @param move Cannot be null. Move to be applied to the Game.
     */
    public void ApplyMove(Move move){
        m_board.ApplyMove(move);
    }
    /**
     * Function used to quit them Game. Sets a boolean flag that is used to determine when Game is over.
     * @param playerID Player who quit the Game. Other player automatically wins.
     * @return Player who wins the Game.
     */
    public int Quit(int playerID){
        m_isOver = true;
        if (playerID == m_p1ID) {
            return m_p2ID;
        } else {
            return m_p1ID;
        }
    }
    /**
     * Call to determine status of Game (over/not over)
     * @return If Game is over or not
     */
    public boolean IsOver(){
        return m_isOver;
    }
    /**
     * Given a player ID returns the other player ID
     * @param playerID Id of one player in game.
     * @return Returns other player in the game based on ID
     */
    public Player getOtherPlayer(int playerID){
        if(playerID == m_p1ID)
            return m_player2;
        else if (playerID == m_p2ID)
            return m_player1;
        else
            return null;
    }

    public void Castle(Color color, boolean left){
        m_board.Castle(color, left);
    }
    public Board getBoard()
    {
        return m_board;
    }
}
