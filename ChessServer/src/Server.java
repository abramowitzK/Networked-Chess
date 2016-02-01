import Game.Game;
import Game.Player;
import Networking.Packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * Created by Kyle on 2/1/2016.
 */
public class Server {
    /**
     * Queue representing players waiting to find match
     * */
    private Queue<Player> m_gameQueue;
    /**
     * Represents the current game being played (we allow only one at a time). Is null if no game is being played
     */
    private Game m_game;

    /**
     * Constructor
     */
    public Server(){
        m_gameQueue = new ConcurrentLinkedQueue<Player>();
        m_game = null;
    }
    /**
     * Main entry point
     * @param args
     */
    public static void main(String[] args){

    }

    /**
     * Process a packet from a player. Logic in here decides what kind of packet it is and what to do with it.
     * @param packet
     */
    public void ProcessPacket(Packet packet){

    }
}
