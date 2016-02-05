package Game;

import Networking.*;

import java.io.IOException;

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
    public Game(Player player1, Player player2){
        m_player1 = player1;
        m_p1ID = player1.GetID();
        m_player2 = player2;
        m_p2ID = player2.GetID();
        m_board = new Board();
    }

    /**
     *
     * @param playerID ID of player that made the move
     * @param move Cannot be null. Move to be applied to the game.
     */
    public void ApplyMove(int playerID, Move move){
        m_board.ApplyMove(move);
        try{
            if(playerID == m_p1ID && !IsOver()){
                //Send to player2
                System.out.println("Sending move to player 2");
                m_player2.GetOut().writeObject(new Packet(OpCode.UpdateBoard, m_p2ID, move));
                //Also let player 1 know that the update went through
                m_player1.GetOut().writeObject(new Packet(OpCode.UpdatedBoard, m_p1ID, move));
            } else if (playerID == m_p2ID && !IsOver()){
                //Send to Player1
                System.out.println("Sending move to player 1");
                m_player1.GetOut().writeObject(new Packet(OpCode.UpdateBoard, m_p1ID, move));
                m_player2.GetOut().writeObject(new Packet(OpCode.UpdatedBoard, m_p2ID, move));
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
    /**
     *
     * @param playerID Player who quit the game. Other player automatically wins.
     * @return Player who wins the game.
     */
    public int Quit(int playerID){
        m_isOver = true;
        if(playerID == m_p1ID){
            return m_p2ID;
        }else{
            return m_p1ID;
        }
    }
    public boolean IsOver(){
        return m_isOver;
    }
}
